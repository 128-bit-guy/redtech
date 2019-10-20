package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.attribute.WSElement;
import _128_bit_guy.redtech.common.attribute.WSElementProvider;
import _128_bit_guy.redtech.common.attribute.WSElementType;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.*;
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WirePart extends AbstractPart implements WSElementProvider {
    private static final double WIRE_WIDTH = 1d / 8d;
    private static final double WIRE_HEIGHT = 1d / 8d;
    private static final Map<Direction, Map<Direction, VoxelShape>> CONNECTION_SHAPES = new EnumMap<>(Direction.class);
    private static ParentNetIdSingle<WirePart> NET_ID = AbstractPart.NET_ID.subType(WirePart.class, RedTech.ID + ":wire");
    private static NetIdDataK<WirePart> UPDATE_CONNECTIONS = NET_ID.idData("update_connections");
    private static VoxelShape[] CENTER_SHAPES = new VoxelShape[6];

    static {
        WireShapeGen.createWireShapes(WIRE_WIDTH, WIRE_HEIGHT, CENTER_SHAPES, CONNECTION_SHAPES);
        UPDATE_CONNECTIONS.setReadWrite(WirePart::receiveUpdateConnections, WirePart::sendUpdateConnections);
        UPDATE_CONNECTIONS.setBuffered(false);
    }

    public final Direction direction;
    public final Map<Direction, Boolean> canConnect;
    public final Map<Direction, Boolean> connected;
    private int ticksExisted = 0;
    private boolean connectibleUpdateScheduled = false;
    private boolean connectionUpdateScheduled = false;
    private boolean connectionUpdateNNTScheduled = false;

    public WirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder);
        direction = Direction.values()[nbt.getInt("dir")];
        canConnect  = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            connected.put(direction, false);
        }
    }
    public WirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder);
        canConnect  = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            connected.put(direction, false);
        }
        direction = buffer.readEnumConstant(Direction.class);
        receiveUpdateConnections(buffer, ctx);
    }

    public WirePart(PartDefinition definition, MultipartHolder holder, Direction direction) {
        super(definition, holder);
        this.direction = direction;
        canConnect  = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            canConnect.put(direction1, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            connected.put(direction1, false);
        }
    }

    public static VoxelShape getWireCenterShape(Direction direction) {
        return CENTER_SHAPES[direction.ordinal()];
    }

    private void receiveUpdateConnections(NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        ctx.assertClientSide();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                connected.put(direction, buf.readBoolean());
            }
        }
        holder.getContainer().recalculateShape();
        holder.getContainer().redrawIfChanged();
    }

    private void sendUpdateConnections(NetByteBuf buf, IMsgWriteCtx ctx) {
        ctx.assertServerSide();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                buf.writeBoolean(connected.get(direction));
            }
        }
    }

    @Override
    public VoxelShape getShape() {
        return getWireCenterShape(direction);
    }

    public static VoxelShape getWireShape(Direction mainDirection, Set<Direction> connections) {
        VoxelShape r = getWireCenterShape(mainDirection);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != mainDirection.getAxis()) {
                if (connections.contains(direction)) {
                    VoxelShape s2 = CONNECTION_SHAPES.get(mainDirection).get(direction);
                    r = VoxelShapes.combine(r, s2, BooleanBiFunction.OR);
                }
            }
        }
        r = r.simplify();
        return r;
    }

    @Override
    public VoxelShape getCollisionShape() {
        return getWireShape(this.direction, mapToSet(connected));
    }

    private static Set<Direction> mapToSet(Map<Direction, Boolean> connected) {
        return connected.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
    }

    @Override
    public PartModelKey getModelKey() {
        return new WireModelKey(this.direction, mapToSet(connected));
    }

    @Override
    public void addAllAttributes(AttributeList<?> list) {
        super.addAllAttributes(list);
        list.offer(this, getShape());
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("dir", direction.ordinal());
        return tag;
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        buffer.writeEnumConstant(direction);
        sendUpdateConnections(buffer, ctx);
    }

    @Override
    public ItemStack getPickStack() {
        return new ItemStack(ModItems.WIRE);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        bus.addListener(this, PartTickEvent.class, this::onTick);
        bus.addListener(this, NeighbourUpdateEvent.class, this::onNeighbourUpdate);
        bus.addListener(this, PartAddedEvent.class, this::onPartAdded);
        bus.addListener(this, PartRemovedEvent.class, this::onPartRemoved);
    }

    private void onTick(PartTickEvent event) {
        if(holder.getContainer().getMultipartWorld().isClient()) {
            return;
        }
        if(connectionUpdateScheduled) {
            refreshConnected();
            connectionUpdateScheduled = false;
        }
        if(connectionUpdateNNTScheduled) {
            connectionUpdateNNTScheduled = false;
            connectionUpdateScheduled = true;
        }
        if(ticksExisted == 1 || connectibleUpdateScheduled) {
            refreshConnectible();
            connectibleUpdateScheduled = false;
        }
        ++ticksExisted;
    }

    private void onNeighbourUpdate(NeighbourUpdateEvent event) {
        connectionUpdateNNTScheduled = true;
    }

    private void onPartAdded(PartAddedEvent event) {
        refreshConnectible();
    }

    private void onPartRemoved(PartRemovedEvent event) {
        connectibleUpdateScheduled = true;
    }

    private void refreshConnectible() {
        if (holder.getContainer().getMultipartWorld().isClient()) {
            return;
        }
        VoxelShape shape = holder.getContainer().getCurrentShape();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == this.direction.getAxis()) {
                continue;
            }
            VoxelShape shape1 = CONNECTION_SHAPES.get(this.direction).get(direction);
            boolean b = VoxelShapes.matchesAnywhere(shape, shape1, BooleanBiFunction.AND);
            canConnect.put(direction, !b);
        }
        connectionUpdateScheduled = true;
    }

    private void refreshConnected() {
        for(Direction direction : Direction.values()) {
            if(direction.getAxis() != this.direction.getAxis()) {
                if(canConnect.get(direction)) {
                    BlockPos pos2 = holder.getContainer().getMultipartPos().offset(direction);
                    World world = holder.getContainer().getMultipartWorld();
                    Optional<WSElement> optional = WSElementProvider.ATTRIBUTE.get(world, pos2).get(this.direction, direction, WSElementType.REDSTONE, null);
                    connected.put(direction, optional.isPresent());
                } else {
                    connected.put(direction, false);
                }
            }
        }
        PlayerStream
                .watching(holder.getContainer().getMultipartBlockEntity())
                .forEach(p -> UPDATE_CONNECTIONS.send(CoreMinecraftNetUtil.getConnection(p), this));
        holder.getContainer().recalculateShape();
    }

    @Override
    public Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color) {
        if(mainDirection != direction) {
            return Optional.empty();
        }
        if(!canConnect.get(searchDirection.getOpposite())) {
            return Optional.empty();
        }
        if(type != WSElementType.REDSTONE) {
            return Optional.empty();
        }
        return Optional.of(new WSElement() {
        });
    }
}
