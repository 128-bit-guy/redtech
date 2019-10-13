package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.init.ModItems;
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
        WireShapeGen.createWireShapes(WIRE_WIDTH, WIRE_HEIGHT, CENTER_SHAPES, CONNECTION_SHAPES);
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
