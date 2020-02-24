package _128_bit_guy.redtech.common.util;

import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;

public class DirectedBlockPointer extends BlockPointerImpl {
    public final Direction direction;

    public DirectedBlockPointer(World world, BlockPos pos, Direction searchDirection) {
        super(world, pos);
        this.direction = searchDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectedBlockPointer that = (DirectedBlockPointer) o;
        return direction == that.direction && getWorld() == that.getWorld() && getBlockPos().equals(that.getBlockPos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld(), getBlockPos(), direction);
    }
}
