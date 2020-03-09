package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.attribute.wire.WSElement;
import _128_bit_guy.redtech.common.attribute.wire.WSElementProvider;
import _128_bit_guy.redtech.common.util.SerializationUtils;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public abstract class WirePartBase extends AbstractPart implements WSElementProvider {
    public static ParentNetIdSingle<WirePartBase> NET_ID = AbstractPart.NET_ID.subType(WirePartBase.class, RedTech.ID + ":wire");
    private static NetIdDataK<WirePartBase> UPDATE_CONNECTIONS = NET_ID.idData("update_connections");

    static {
        UPDATE_CONNECTIONS.setReadWrite(WirePartBase::receiveUpdateConnections, WirePartBase::sendUpdateConnections);
    }

    public final Direction direction;
    public final Map<Direction, Boolean> canConnect;
    public final Map<Direction, Boolean> connected;
//    public final Map<Direction, WSElement> connections = new EnumMap<>(Direction.class);
    private final Map<Direction, Map<Direction, VoxelShape>> connectionShapes;
    private final VoxelShape[] centerShapes;
    private final VoxelShape[] notConnectedShapes;
    protected boolean connectibleUpdateScheduled = false;
    protected boolean connectionSendScheduled = false;

    public WirePartBase(PartDefinition definition, MultipartHolder holder, CompoundTag nbt, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes, VoxelShape[] notConnectedShapes) {
        super(definition, holder);
        direction = Direction.values()[nbt.getInt("dir")];
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        this.notConnectedShapes = notConnectedShapes;
        canConnect = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            canConnect.put(direction, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            connected.put(direction, false);
        }
        SerializationUtils.deserializeEnumToBoolean(canConnect, nbt.getByte("canConnect"), Direction.class);
        SerializationUtils.deserializeEnumToBoolean(connected, nbt.getByte("connected"), Direction.class);
    }

    public WirePartBase(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes, VoxelShape[] notConnectedShapes) throws InvalidInputDataException {
        super(definition, holder);
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        this.notConnectedShapes = notConnectedShapes;
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

    public WirePartBase(PartDefinition definition, MultipartHolder holder, Direction direction, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] centerShapes, VoxelShape[] notConnectedShapes) {
        super(definition, holder);
        this.direction = direction;
        this.connectionShapes = connectionShapes;
        this.centerShapes = centerShapes;
        this.notConnectedShapes = notConnectedShapes;
        canConnect = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            canConnect.put(direction1, false);
        }
        connected = new EnumMap<>(Direction.class);
        for (Direction direction1 : Direction.values()) {
            connected.put(direction1, false);
        }
    }

    private boolean canExist() {
        World world = holder.getContainer().getMultipartWorld();
        BlockPos pos = holder.getContainer().getMultipartPos();
        return canExist(world, pos, direction);
    }

    public static boolean canExist(World world, BlockPos pos, Direction direction) {
        BlockPos pos2 = pos.offset(direction);
        BlockState bs = world.getBlockState(pos2);
        return bs.isSideSolidFullSquare(world, pos2, direction.getOpposite()) || bs.getBlock() == Blocks.HOPPER;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("dir", direction.ordinal());
        tag.putByte("canConnect", SerializationUtils.serializeEnumToBoolean(canConnect, Direction.class));
        tag.putByte("connected", SerializationUtils.serializeEnumToBoolean(connected, Direction.class));
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

    protected void onTick(PartTickEvent event) {
        if (holder.getContainer().getMultipartWorld().isClient()) {
            return;
        }
        if (connectionSendScheduled) {
            sendUpdateConnections();
            connectionSendScheduled = false;
        }
        if(connectibleUpdateScheduled) {
            refreshConnectible();
            connectibleUpdateScheduled = false;
        }
    }

    private void onNeighbourUpdate(NeighbourUpdateEvent event) {
        if(!canExist()) {
            holder.remove();
        }
        updateEverything();
    }

    protected void updatePower() {

    }

    protected void updateEverything() {
        refreshConnected();
        updatePower();
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
        boolean x = false;
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == this.direction.getAxis()) {
                continue;
            }
            VoxelShape shape1 = connectionShapes.get(this.direction).get(direction);
            boolean b = !VoxelShapes.matchesAnywhere(shape, shape1, BooleanBiFunction.AND);
            x |= b != canConnect.get(direction);
            canConnect.put(direction, b);
        }
        updateEverything();
        if(x) {
            BlockPos pos = holder.getContainer().getMultipartPos();
            World w = holder.getContainer().getMultipartWorld();
            w.updateNeighbors(pos, w.getBlockState(pos).getBlock());
        }
    }

    protected Optional<WSElement> getConnectedWire(Direction direction) {
        BlockPos pos2 = holder.getContainer().getMultipartPos().offset(direction);
        World world = holder.getContainer().getMultipartWorld();
        WirePointer wp = new WirePointer(world, pos2, this.direction, direction);
        return getConnectedElement(wp);
    }

    private void refreshConnected() {
        boolean removed = false;
        boolean added = false;
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.direction.getAxis()) {
                if (canConnect.get(direction)) {
                    Optional<WSElement> element = getConnectedWire(direction);
                    added |= !connected.get(direction) && element.isPresent();
                    removed |= connected.get(direction) && !element.isPresent();
                    connected.put(direction, element.isPresent());
//                    if (element.isPresent()) {
//                        if (!connections.containsKey(direction)) {
//                            connections.put(direction, element.get());
//                            added = true;
//                        }
//                    } else {
//                        if (connections.containsKey(direction)) {
//                            connections.remove(direction);
//                            removed = true;
//                        }
//                    }

                } else {
                    removed |= !connected.get(direction);
                    connected.put(direction, false);
//                    if (connections.containsKey(direction)) {
//                        connections.remove(direction);
//                        removed = true;
//                    }
                }
            }
        }
        if (added || removed) {
            onConnectionsModified(removed);
        }
        connectionSendScheduled = true;
        holder.getContainer().recalculateShape();
    }

    private void sendUpdateConnections() {
        PlayerStream
                .watching(holder.getContainer().getMultipartBlockEntity())
                .forEach(p -> UPDATE_CONNECTIONS.send(CoreMinecraftNetUtil.getConnection(p), this));
    }

    public abstract Optional<WSElement> getConnectedElement(WirePointer ptr);

    public abstract void onConnectionsModified(boolean removed);

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
        return WireShapeGen.getWireShape(this.direction, WireShapeGen.mapToSet(connected), centerShapes, connectionShapes, notConnectedShapes);
    }

    @Override
    public void onPlacedBy(PlayerEntity player, Hand hand) {
        super.onPlacedBy(player, hand);
        refreshConnectible();
    }
}
