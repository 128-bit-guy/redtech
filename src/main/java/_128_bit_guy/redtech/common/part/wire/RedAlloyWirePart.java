package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.attribute.wire.WSElement;
import _128_bit_guy.redtech.common.attribute.wire.WSElementProvider;
import _128_bit_guy.redtech.common.attribute.wire.WSElementType;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.part.DynamicRedstonePart;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import _128_bit_guy.redtech.common.util.SerializationUtils;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.*;

public class RedAlloyWirePart extends WirePartBase implements DynamicRedstonePart {
    public static ParentNetIdSingle<RedAlloyWirePart> NET_ID = WirePartBase.NET_ID.subType(RedAlloyWirePart.class, RedTech.ID + ":red_alloy_wire");
    private static NetIdDataK<RedAlloyWirePart> UPDATE_POWER = NET_ID.idData("update_power");

    static {
        UPDATE_POWER.setReadWrite(RedAlloyWirePart::receiveUpdatePower, RedAlloyWirePart::sendUpdatePower);
    }

    private static final double WIRE_WIDTH = 1d / 8d;
    private static final double WIRE_HEIGHT = 1d / 8d;
    private static final Map<Direction, Map<Direction, VoxelShape>> CONNECTION_SHAPES = new EnumMap<>(Direction.class);
    private static VoxelShape[] CENTER_SHAPES = new VoxelShape[6];
    private static VoxelShape[] NOT_CONNECTED_SHAPES = new VoxelShape[6];
    private int power;
    private boolean powerSendScheduled = false;
    private static boolean wiresEmitRedstonePower = true;
    private final DyeColor color;

    static {
        WireShapeGen.createWireShapes(WIRE_WIDTH, WIRE_HEIGHT, CENTER_SHAPES, CONNECTION_SHAPES, NOT_CONNECTED_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder, nbt, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
        power = nbt.getInt("strength");
        color = SerializationUtils.intToEnum(nbt.getInt("color"), DyeColor.class);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder, buffer, ctx, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
        receiveUpdatePower(buffer, ctx);
        color = SerializationUtils.intToEnum(buffer.readVarInt(), DyeColor.class);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, Direction direction, DyeColor color) {
        super(definition, holder, direction, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
        this.color = color;
    }

    public static VoxelShape getWireShape(Direction mainDirection, Set<Direction> connections) {
        return WireShapeGen.getWireShape(mainDirection, connections, CENTER_SHAPES, CONNECTION_SHAPES, NOT_CONNECTED_SHAPES);
    }

    @Override
    public PartModelKey getModelKey() {
        return new WireModelKey(this.direction, WireShapeGen.mapToSet(connected), power);
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        sendUpdatePower(buffer, ctx);
        buffer.writeVarInt(SerializationUtils.enumToInt(color));
    }

    @Override
    public ItemStack getPickStack() {
        if(color != null) {
            return new ItemStack(ModItems.COLORED_RED_ALLOY_WIRES.get(color));
        }
        return new ItemStack(ModItems.RED_ALLOY_WIRE);
    }

    @Override
    public Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color) {
        if (mainDirection != direction) {
            return Optional.empty();
        }
        if((color != null) && (this.color != null) && (this.color != color)) {
            return Optional.empty();
        }
        if (searchDirection != null && !canConnect.get(searchDirection.getOpposite())) {
            return Optional.empty();
        }
        if (type != WSElementType.REDSTONE) {
            return Optional.empty();
        }
        return Optional.of(new RAWSElement() {
            @Override
            public int getPower() {
                RedAlloyWirePart wp = RedAlloyWirePart.this;
                return wp.power;
            }

            @Override
            public int getIncomingRedstonePower() {
                return getIncomingPower();
            }

            @Override
            public void setPower(int strength) {
                if(RedAlloyWirePart.this.power != strength) {
                    RedAlloyWirePart.this.power = strength;
                    powerSendScheduled = true;
                    updateAllPowerReceivers();
                }
            }

            @Override
            public Map<Direction, WSElement> getConnections() {
                EnumMap<Direction, WSElement> result = new EnumMap<>(Direction.class);
                for(Direction direction : Direction.values()) {
                    if(connected.get(direction)) {
                        Optional<WSElement> element = getConnectedWire(direction);
                        element.ifPresent(wsElement -> result.put(direction, wsElement));
                    }
                }
                return result;
            }

            @Override
            public WirePointer getPtr() {
                return RedAlloyWirePart.this.getPtr();
            }
        });
    }

    private void updateAllPowerReceivers() {
        World world = holder.getContainer().getMultipartWorld();
        BlockPos pos = holder.getContainer().getMultipartPos();
        world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
        BlockPos pos2 = pos.offset(direction);
        world.updateNeighbors(pos2, world.getBlockState(pos2).getBlock());
    }

    private int getIncomingPower() {
        if(color != null) {
            return 0;
        }
        boolean lastWiresEmitRedstonePower = wiresEmitRedstonePower;
        wiresEmitRedstonePower = false;
        int result = (holder.getContainer().getMultipartWorld().getReceivedRedstonePower(holder.getContainer().getMultipartPos()) * 17);
        wiresEmitRedstonePower = lastWiresEmitRedstonePower;
        return result;
    }



    @Override
    protected void updatePower() {
        int oldPower = power;
        int newPower = getIncomingPower();
        for(Direction direction : Direction.values()) {
            if(direction.getAxis() == this.direction.getAxis()) {
                continue;
            }
            Optional<WSElement> newElement = getConnectedWire(direction);
            if(newElement.isPresent()) {
                newPower = Math.max(newPower, ((RAWSElement)newElement.get()).getPower() - 1);
            }
        }
        if(newPower == oldPower) {
            return;
        }
        RAWSElement el = (RAWSElement)get(direction, null, WSElementType.REDSTONE, null).get();
        WirePowerPropagator.propagate(el, oldPower > newPower);
        if(oldPower != power) {
            powerSendScheduled = true;
        }
    }

    private WirePointer getPtr() {
        return new WirePointer(holder.getContainer().getMultipartWorld(), holder.getContainer().getMultipartPos(), direction, null);
    }

    @Override
    public Optional<WSElement> getConnectedElement(WirePointer ptr) {
        return WSElementProvider.getFromPtr(ptr, WSElementType.REDSTONE, color);
    }

    @Override
    public void onConnectionsModified(boolean removed) {
        RAWSElement el = (RAWSElement)get(direction, null, WSElementType.REDSTONE, null).get();
        WirePowerPropagator.propagate(el, removed);
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.world.isClient()) {
            return ActionResult.PASS;
        }
        if(player.getStackInHand(hand).getItem() != ModItems.MULTIMETER) {
            return ActionResult.FAIL;
        }
        player.sendMessage(new LiteralText("Power: " + power));
        return ActionResult.SUCCESS;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("strength", power);
        tag.putInt("color", SerializationUtils.enumToInt(color));
        return tag;
    }

    @Override
    protected void onTick(PartTickEvent event) {
        super.onTick(event);
        if(powerSendScheduled) {
            sendNetworkUpdate(this, UPDATE_POWER);
            powerSendScheduled = false;
        }
    }


    private void receiveUpdatePower(NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        ctx.assertClientSide();
        power = buf.readVarInt();
//        holder.getContainer().recalculateShape();
        holder.getContainer().redrawIfChanged();
    }

    private void sendUpdatePower(NetByteBuf buf, IMsgWriteCtx ctx) {
        ctx.assertServerSide();
        buf.writeVarInt(power);
    }

    @Override
    public int getStrongRedstonePower(Direction facing) {
        if(color != null) {
            return 0;
        }
        if((facing.getAxis() == this.direction.getAxis()) || (!wiresEmitRedstonePower)) {
            return 0;
        }
        return power / 17;
    }

    @Override
    public int getWeakRedstonePower(Direction facing) {
        if(color != null) {
            return 0;
        }
        if((facing != this.direction) || (!wiresEmitRedstonePower)) {
            return 0;
        }
        return power / 17;
    }
}
