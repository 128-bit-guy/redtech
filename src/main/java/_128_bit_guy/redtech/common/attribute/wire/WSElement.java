package _128_bit_guy.redtech.common.attribute.wire;

import _128_bit_guy.redtech.common.part.wire.WirePointer;
import net.minecraft.util.math.Direction;

import java.util.Map;

public interface WSElement {
    Map<Direction, WSElement> getConnections();

    WirePointer getPtr();
}
