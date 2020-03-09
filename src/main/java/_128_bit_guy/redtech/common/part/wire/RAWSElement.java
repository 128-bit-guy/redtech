package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.attribute.wire.WSElement;

public interface RAWSElement extends WSElement {
    int getPower();
    int getIncomingRedstonePower();
    void setPower(int strength);
}
