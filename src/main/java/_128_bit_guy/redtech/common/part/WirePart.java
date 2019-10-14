package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.init.ModItems;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.*;
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.EnumMap;
import java.util.Map;

public class WirePart extends AbstractPart {
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
    private int ticksExisted = 0;

    public WirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder);
        direction = Direction.values()[nbt.getInt("dir")];
        canConnect  = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
        }
    }
    public WirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder);
        canConnect  = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
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
    }

    public static VoxelShape getWireCenterShape(Direction direction) {
        return CENTER_SHAPES[direction.ordinal()];
    }

    private void receiveUpdateConnections(NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        ctx.assertClientSide();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                canConnect.put(direction, buf.readBoolean());
            }
        }
        holder.getContainer().recalculateShape();
    }

    private void sendUpdateConnections(NetByteBuf buf, IMsgWriteCtx ctx) {
        ctx.assertServerSide();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                buf.writeBoolean(canConnect.get(direction));
            }
        }
    }

    @Override
    public VoxelShape getShape() {
        return getWireCenterShape(direction);
    }

    @Override
    public VoxelShape getCollisionShape() {
        VoxelShape r = CENTER_SHAPES[direction.ordinal()];
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                if (canConnect.get(direction)) {
                    VoxelShape s2 = CONNECTION_SHAPES.get(this.direction).get(direction);
                    r = VoxelShapes.combine(r, s2, BooleanBiFunction.OR);
                }
            }
        }
        return r;
    }

    @Override
    public PartModelKey getModelKey() {
        return new PartModelKey() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }

    @Override
    public void addAllAttributes(AttributeList<?> list) {
        super.addAllAttributes(list);

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
    }

    private void onTick(PartTickEvent event) {
        if(holder.getContainer().getMultipartWorld().isClient()) {
            return;
        }
        if(ticksExisted == 1) {
            refreshConnectible();
        }
        ++ticksExisted;
    }

    private void onNeighbourUpdate(NeighbourUpdateEvent event) {
        refreshConnectible();
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
        PlayerStream
                .watching(holder.getContainer().getMultipartBlockEntity())
                .forEach(p -> UPDATE_CONNECTIONS.send(CoreMinecraftNetUtil.getConnection(p), this));
    }
}
