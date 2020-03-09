package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.attribute.wire.WSElement;
import _128_bit_guy.redtech.common.attribute.wire.WSElementProvider;
import _128_bit_guy.redtech.common.attribute.wire.WSElementType;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.*;

public class RedAlloyWirePart extends WirePartBase {
    private static final double WIRE_WIDTH = 1d / 8d;
    private static final double WIRE_HEIGHT = 1d / 8d;
    private static final Map<Direction, Map<Direction, VoxelShape>> CONNECTION_SHAPES = new EnumMap<>(Direction.class);
    private static VoxelShape[] CENTER_SHAPES = new VoxelShape[6];
    private static VoxelShape[] NOT_CONNECTED_SHAPES = new VoxelShape[6];
    private int power;

    static {
        WireShapeGen.createWireShapes(WIRE_WIDTH, WIRE_HEIGHT, CENTER_SHAPES, CONNECTION_SHAPES, NOT_CONNECTED_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder, nbt, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
        power = nbt.getInt("strength");
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder, buffer, ctx, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, Direction direction) {
        super(definition, holder, direction, CONNECTION_SHAPES, CENTER_SHAPES, NOT_CONNECTED_SHAPES);
    }

    public static VoxelShape getWireShape(Direction mainDirection, Set<Direction> connections) {
        return WireShapeGen.getWireShape(mainDirection, connections, CENTER_SHAPES, CONNECTION_SHAPES, NOT_CONNECTED_SHAPES);
    }

    @Override
    public PartModelKey getModelKey() {
        return new WireModelKey(this.direction, WireShapeGen.mapToSet(connected), power);
    }

    @Override
    public ItemStack getPickStack() {
        return new ItemStack(ModItems.WIRE);
    }

    @Override
    public Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color) {
        if (mainDirection != direction) {
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
                return power;
            }

            @Override
            public void setPower(int strength) {
                RedAlloyWirePart.this.power = strength;
            }

            @Override
            public Map<Direction, WSElement> getConnections() {
                return connections;
            }

            @Override
            public WirePointer getPtr() {
                return RedAlloyWirePart.this.getPtr();
            }
        });
    }

    @Override
    protected void onNeighbourUpdate(NeighbourUpdateEvent event) {
        super.onNeighbourUpdate(event);
        power = (holder.getContainer().getMultipartWorld().getReceivedRedstonePower(holder.getContainer().getMultipartPos()) * 17);
    }

    private WirePointer getPtr() {
        return new WirePointer(holder.getContainer().getMultipartWorld(), holder.getContainer().getMultipartPos(), direction, null);
    }

    @Override
    public Optional<WSElement> getConnectedElement(WirePointer ptr) {
        return WSElementProvider.getFromPtr(ptr, WSElementType.REDSTONE, null);
    }

    private void zeroingDfs(RAWSElement element, Set<WirePointer> was, Set<WirePointer> surrounding) {
        if(was.contains(element.getPtr())) {
            return;
        }
        was.add(element.getPtr());
        int wasPower = element.getPower();
        element.setPower(0);
        for(WSElement element1 : element.getConnections().values()) {
            if(element1 instanceof RAWSElement) {
                RAWSElement element2 = (RAWSElement)element1;
                if(element2.getPower() < wasPower && element2.getPower() != 0) {
                    zeroingDfs(element2, was, surrounding);
                } else {
                    surrounding.add(element2.getPtr());
                }
            }
        }
        surrounding.remove(element.getPtr());
    }

    @Override
    public void onConnectionsModified(boolean removed) {
        RAWSElement el = (RAWSElement)get(direction, null, WSElementType.REDSTONE, null).get();
        HashSet<WirePointer> fillStartPoints = new HashSet<>();
        if(removed) {
            zeroingDfs(el, new HashSet<>(), fillStartPoints);
        }

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
        return tag;
    }


}
