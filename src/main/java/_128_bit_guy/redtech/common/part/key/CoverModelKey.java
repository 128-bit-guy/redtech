package _128_bit_guy.redtech.common.part.key;

import alexiil.mc.lib.multipart.api.render.PartModelKey;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.Set;

public class CoverModelKey extends PartModelKey {
    public final Direction direction;
    public final int size;
    public final BlockState state;
    public final Set<Direction> culled;

    public CoverModelKey(Direction direction, int size, BlockState state, Set<Direction> culled) {
        this.direction = direction;
        this.size = size;
        this.state = state;
        this.culled = culled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoverModelKey that = (CoverModelKey) o;
        return size == that.size &&
                direction == that.direction &&
                state.equals(that.state) &&
                culled.equals(that.culled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, size, state, culled);
    }
}
