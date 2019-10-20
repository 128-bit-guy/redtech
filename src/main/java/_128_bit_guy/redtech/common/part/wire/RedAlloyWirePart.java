package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.attribute.wire.WSElement;
import _128_bit_guy.redtech.common.attribute.wire.WSElementProvider;
import _128_bit_guy.redtech.common.attribute.wire.WSElementType;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RedAlloyWirePart extends WirePartBase {
    private static final double WIRE_WIDTH = 1d / 8d;
    private static final double WIRE_HEIGHT = 1d / 8d;
    private static final Map<Direction, Map<Direction, VoxelShape>> CONNECTION_SHAPES = new EnumMap<>(Direction.class);
    private static VoxelShape[] CENTER_SHAPES = new VoxelShape[6];
    private static RANetwork network;

    static {
        WireShapeGen.createWireShapes(WIRE_WIDTH, WIRE_HEIGHT, CENTER_SHAPES, CONNECTION_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder, nbt, CONNECTION_SHAPES, CENTER_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder, buffer, ctx, CONNECTION_SHAPES, CENTER_SHAPES);
    }

    public RedAlloyWirePart(PartDefinition definition, MultipartHolder holder, Direction direction) {
        super(definition, holder, direction, CONNECTION_SHAPES, CENTER_SHAPES);
    }

    public static VoxelShape getWireShape(Direction mainDirection, Set<Direction> connections) {
        return WireShapeGen.getWireShape(mainDirection, connections, CENTER_SHAPES, CONNECTION_SHAPES);
    }

    @Override
    public PartModelKey getModelKey() {
        return new WireModelKey(this.direction, WireShapeGen.mapToSet(connected));
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
        if (!canConnect.get(searchDirection.getOpposite())) {
            return Optional.empty();
        }
        if (type != WSElementType.REDSTONE) {
            return Optional.empty();
        }
        return Optional.of(new WSElement() {
        });
    }

    @Override
    public Optional<WSElement> shouldConnect(WirePointer ptr) {
        return WSElementProvider.getFromPtr(ptr, WSElementType.REDSTONE, null);
    }

    @Override
    public void onConnectionsModified(boolean removed) {

    }

    @Override
    public boolean onActivate(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.world.isClient()) {
            return false;
        }
        if(player.getStackInHand(hand).getItem() != ModItems.MULTIMETER) {
            return false;
        }

        return true;
    }
}
