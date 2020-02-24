package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.part.key.CoverModelKey;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.util.ShapeMath;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.property.MultipartProperties;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.NbtHelper;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.EnumSet;

public class CoverPart extends AbstractPart {
    private static VoxelShape[][] SHAPES = new VoxelShape[7][6];

    static {
        for (int i = 0; i < 7; ++i) {
            int size = 2 * (i + 1);
            VoxelShape base = ShapeMath.intCube(0, 0, 0, 16, size, 16);
            for (Direction direction : Direction.values()) {
                SHAPES[i][direction.ordinal()] = ShapeMath.rotate(base, Direction.DOWN, direction);
            }
        }
    }

    private final int size;
    private final Direction direction;
    private final BlockState state;
    private int ticked = 0;
    private EnumSet<Direction> culled = EnumSet.noneOf(Direction.class);

    public CoverPart(PartDefinition definition, MultipartHolder holder, int size, Direction direction, BlockState state) {
        super(definition, holder);
        this.size = size;
        this.direction = direction;
        this.state = state;
        postInit();
    }

    public CoverPart(PartDefinition definition, MultipartHolder holder, CompoundTag nbt) {
        super(definition, holder);
        size = nbt.getInt("size");
        direction = Direction.values()[nbt.getInt("dir")];
        state = NbtHelper.toBlockState(nbt.getCompound("state"));
        postInit();
    }

    public CoverPart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder);
        size = buffer.readFixedBits(3);
        direction = buffer.readEnumConstant(Direction.class);
        state = NbtHelper.toBlockState(buffer.readCompoundTag());
        postInit();
    }

    private void postInit() {
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        holder.enqueueFirstTickTask(() -> {
            holder.getContainer().getProperties().setValue(this, MultipartProperties.LIGHT_VALUE, state.getLuminance());
            if (holder.getContainer().isClientWorld()) {
                bus.addListener(this, PartTickEvent.class, this::onTickClient);
                bus.addListener(this, PartAddedEvent.class, this::onPartAddedClient);
                bus.addListener(this, PartRemovedEvent.class, this::onPartRemovedClient);
                for (AbstractPart part : holder.getContainer().getAllParts()) {
                    onPartAddedClient(new PartAddedEvent(part));
                }
            }
        });
    }

    private void onPartAddedClient(PartAddedEvent event) {
        if(event.part instanceof CoverPart) {
            CoverPart cp = (CoverPart) event.part;
            if(cp.direction.getAxis() != direction.getAxis()) {
                culled.add(cp.direction);
                holder.getContainer().redrawIfChanged();
            }
        }
    }

    private void onPartRemovedClient(PartRemovedEvent event) {
        if(event.removed instanceof CoverPart) {
            CoverPart cp = (CoverPart) event.removed;
            if(cp.direction.getAxis() != direction.getAxis()) {
                culled.remove(cp.direction);
                holder.getContainer().redrawIfChanged();
            }
        }
    }

    private void onTickClient(PartTickEvent event) {
        if(ticked < 2) {

            ++ticked;
        }
    }

    public static VoxelShape getCoverShape(int size, Direction direction) {
        return SHAPES[size][direction.ordinal()];
    }

    @Override
    public VoxelShape getShape() {
        return getCoverShape(size, direction);
    }

    @Override
    public PartModelKey getModelKey() {
        return new CoverModelKey(direction, size, state, EnumSet.copyOf(culled));
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        buffer.writeFixedBits(size, 3);
        buffer.writeEnumConstant(direction);
        buffer.writeCompoundTag(NbtHelper.fromBlockState(state));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", size);
        tag.putInt("dir", direction.ordinal());
        tag.put("state", NbtHelper.fromBlockState(state));
        return tag;
    }

    @Override
    public boolean canOverlapWith(AbstractPart other) {
        return other instanceof CoverPart;
    }

    @Override
    public boolean isBlocking(Direction searchDirection) {
        return searchDirection == direction.getOpposite();
    }

    @Override
    public ItemStack getPickStack() {
        return ModItems.COVER.createStack(size, state);
    }


}
