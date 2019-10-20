package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.attribute.wire.WSElementProvider;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.net.*;
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;

public abstract class WirePartBase extends AbstractPart implements WSElementProvider {
    private static ParentNetIdSingle<WirePartBase> NET_ID = AbstractPart.NET_ID.subType(WirePartBase.class, RedTech.ID + ":wire");
    private static NetIdDataK<WirePartBase> UPDATE_CONNECTIONS = NET_ID.idData("update_connections");

    static {
        UPDATE_CONNECTIONS.setReadWrite(WirePartBase::receiveUpdateConnections, WirePartBase::sendUpdateConnections);
        UPDATE_CONNECTIONS.setBuffered(false);
    }

    public final Direction direction;
    public final Map<Direction, Boolean> canConnect;
    public final Map<Direction, Boolean> connected;
    private final Map<Direction, Map<Direction, VoxelShape>> connectionShapes;
    private final VoxelShape[] centerShapes;
    protected int ticksExisted = 0;
    protected boolean connectibleUpdateScheduled = false;
    protected boolean connectionUpdateScheduled = false;
    protected boolean connectionUpdateNNTScheduled = false;

    public WirePartBase(PartDefinition definition, MultipartHolder holder, CompoundTag nbt, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes) {
        super(definition, holder);
        direction = Direction.values()[nbt.getInt("dir")];
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        canConnect = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            connected.put(direction, false);
        }
    }

    public WirePartBase(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes) throws InvalidInputDataException {
        super(definition, holder);
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        canConnect = new EnumMap<>(Direction.class);
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

    public WirePartBase(PartDefinition definition, MultipartHolder holder, Direction direction, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes) {
        super(definition, holder);
        this.direction = direction;
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        canConnect = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            canConnect.put(direction1, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            connected.put(direction1, false);
        }
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("dir", direction.ordinal());
        return tag;
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
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        buffer.writeEnumConstant(direction);
        sendUpdateConnections(buffer, ctx);
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
        if (holder.getContainer().getMultipartWorld().isClient()) {
            return;
        }
        if (connectionUpdateScheduled) {
            refreshConnected();
            connectionUpdateScheduled = false;
        }
        if (connectionUpdateNNTScheduled) {
            connectionUpdateNNTScheduled = false;
            connectionUpdateScheduled = true;
        }
        if (ticksExisted == 1 || connectibleUpdateScheduled) {
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
            VoxelShape shape1 = connectionShapes.get(this.direction).get(direction);
            boolean b = VoxelShapes.matchesAnywhere(shape, shape1, BooleanBiFunction.AND);
            canConnect.put(direction, !b);
        }
        connectionUpdateScheduled = true;
    }

    private void refreshConnected() {
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                if (canConnect.get(direction)) {
                    BlockPos pos2 = holder.getContainer().getMultipartPos().offset(direction);
                    World world = holder.getContainer().getMultipartWorld();
//                    Optional<WSElement> optional = WSElementProvider.ATTRIBUTE.get(world, pos2).get(this.direction, direction, WSElementType.REDSTONE, null);
//                    connected.put(direction, optional.isPresent());
                    WirePointer wp = new WirePointer(world, pos2, this.direction, direction);
                    connected.put(direction, shouldConnect(wp));

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

    public abstract boolean shouldConnect(WirePointer ptr);

    @Override
    public void addAllAttributes(AttributeList<?> list) {
        super.addAllAttributes(list);
        list.offer(this, getShape());
    }

    @Override
    public VoxelShape getShape() {
        return WireShapeGen.getWireCenterShape(direction, centerShapes);
    }

    @Override
    public VoxelShape getCollisionShape() {
        return WireShapeGen.getWireShape(this.direction, WireShapeGen.mapToSet(connected), centerShapes, connectionShapes);
    }
}
