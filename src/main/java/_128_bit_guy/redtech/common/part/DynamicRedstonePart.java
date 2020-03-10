package _128_bit_guy.redtech.common.part;

import net.minecraft.util.math.Direction;

public interface DynamicRedstonePart {
    int getStrongRedstonePower(Direction facing);

    int getWeakRedstonePower(Direction facing);
}
