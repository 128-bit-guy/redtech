package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.util.ShapeMath;
import _128_bit_guy.redtech.common.util.VecMath;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.EnumMap;
import java.util.Map;

public class WirePart extends AbstractPart {
    public static VoxelShape[] CENTER_SHAPES = new VoxelShape[6];
    public static final double WIRE_WIDTH = 1d / 8d;
    public static final double WIRE_HEIGHT = 1d / 8d;
    public static final Map<Direction, Map<Direction, VoxelShape>> CONNECTION_SHAPES = new EnumMap<>(Direction.class);

    static {
        double halfWidth = WIRE_WIDTH / 2;
        {
            VoxelShape down = VoxelShapes.cuboid(0.5 - halfWidth, 0, 0.5 - halfWidth, 0.5 + halfWidth, WIRE_HEIGHT, 0.5 + halfWidth);
            for (Direction direction : Direction.values()) {
                CENTER_SHAPES[direction.ordinal()] = ShapeMath.rotate(down, Direction.DOWN, direction);
            }
        }
        {
            for(Direction direction : Direction.values()) {
                CONNECTION_SHAPES.put(direction, new EnumMap<>(Direction.class));
            }
            VoxelShape downWest = VoxelShapes.cuboid(0, 0, 0.5 - halfWidth, 0.5 - halfWidth, WIRE_HEIGHT, 0.5 + halfWidth);
            for (Direction horizontalDirection : Direction.values()) {
                if (horizontalDirection.getAxis() == Direction.Axis.Y) {
                    continue;
                }
                VoxelShape down = ShapeMath.rotate(downWest, Direction.WEST, horizontalDirection, Direction.Axis.Y);
                for(Direction mainDirection : Direction.values()) {
                    Direction addDirection = VecMath.rotateDirection(horizontalDirection, Direction.DOWN, mainDirection);
                    VoxelShape sh = ShapeMath.rotate(down, Direction.DOWN, mainDirection);
                    CONNECTION_SHAPES.get(mainDirection).put(addDirection, sh);
                }
            }
        }
    }

    public final Direction direction;

    public WirePart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder);
        direction = Direction.values()[nbt.getInt("dir")];
    }

    public WirePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) {
        super(definition, holder);
        direction = buffer.readEnumConstant(Direction.class);
    }

    public WirePart(PartDefinition definition, MultipartHolder holder, Direction direction) {
        super(definition, holder);
        this.direction = direction;
    }

    @Override
    public VoxelShape getShape() {
        return getWireCenterShape(direction);
    }

    @Override
    public VoxelShape getCollisionShape() {
        Direction direction = Direction.DOWN;
        if(this.direction.getAxis() == direction.getAxis()) {
            direction = Direction.WEST;
        }
        VoxelShape connectionShape = CONNECTION_SHAPES.get(this.direction).get(direction);
        return VoxelShapes.combine(super.getCollisionShape(), connectionShape, BooleanBiFunction.OR);
    }

    public static VoxelShape getWireCenterShape(Direction direction) {
        return CENTER_SHAPES[direction.ordinal()];
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
    }

    @Override
    public ItemStack getPickStack() {
        return new ItemStack(ModItems.WIRE);
    }
}
