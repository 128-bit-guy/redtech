package _128_bit_guy.redtech.common.item.preview;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import net.minecraft.block.BlockState;
//import net.minecraft.util.TagHelper;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CoverPreviewKey implements PreviewRendererKey {
    public BlockPos pos;
    public BlockState state;
    public Direction direction;
    public int size;

    @Override
    public boolean equals(PreviewRendererKey k) {
        return false;
    }

    @Override
    public void receive(NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        try {
            pos = buffer.readBlockPos();
            state = NbtHelper.toBlockState(buffer.readCompoundTag());
            direction = buffer.readEnumConstant(Direction.class);
            size = buffer.readVarInt();
        } catch (NullPointerException ex) {
            throw new InvalidInputDataException();
        }
    }

    @Override
    public void write(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeBlockPos(pos);
        buffer.writeCompoundTag(NbtHelper.fromBlockState(state));
        buffer.writeEnumConstant(direction);
        buffer.writeVarInt(size);
    }

    @Override
    public String toString() {
        return "CoverPreviewKey{" +
                "pos=" + pos +
                ", state=" + state +
                ", direction=" + direction +
                ", size=" + size +
                '}';
    }
}
