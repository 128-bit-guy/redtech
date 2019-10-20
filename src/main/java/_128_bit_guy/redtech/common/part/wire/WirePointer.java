package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.util.DirectedBlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WirePointer extends DirectedBlockPointer {
    public final Direction mainDirection;

    public WirePointer(World world, BlockPos pos, Direction mainDirection, Direction searchDirection) {
        super(world, pos, searchDirection);
        this.mainDirection = mainDirection;
    }
}
