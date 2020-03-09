package _128_bit_guy.redtech.common.part.key;

import alexiil.mc.lib.multipart.api.render.PartModelKey;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.Set;

public class WireModelKey extends PartModelKey {
    public final Direction mainDirection;
    public final Set<Direction> connections;
    public final int power;

    public WireModelKey(Direction mainDirection, Set<Direction> connections) {
        this.mainDirection = mainDirection;
        this.connections = connections;
        this.power = 0;
    }

    public WireModelKey(Direction mainDirection, Set<Direction> connections, int power) {
        this.mainDirection = mainDirection;
        this.connections = connections;
        this.power = power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WireModelKey that = (WireModelKey) o;
        return mainDirection == that.mainDirection &&
                connections.equals(that.connections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainDirection, connections);
    }
}
