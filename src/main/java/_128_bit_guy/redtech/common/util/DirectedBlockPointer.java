package _128_bit_guy.redtech.common.util;

import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DirectedBlockPointer extends BlockPointerImpl {
    public final Direction direction;

    public DirectedBlockPointer(World world, BlockPos pos, Direction searchDirection) {
        super(world, pos);
        this.direction = searchDirection;
    }
}
