package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.util.DirectedBlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;

public class WirePointer extends DirectedBlockPointer {
    public final Direction mainDirection;

    public WirePointer(World world, BlockPos pos, Direction mainDirection, Direction searchDirection) {
        super(world, pos, searchDirection);
        this.mainDirection = mainDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WirePointer that = (WirePointer) o;
        return mainDirection == that.mainDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mainDirection);
    }
}
