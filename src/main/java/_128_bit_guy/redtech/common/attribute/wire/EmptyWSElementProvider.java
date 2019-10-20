package _128_bit_guy.redtech.common.attribute.wire;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public enum EmptyWSElementProvider implements WSElementProvider {
    INSTANCE;

    @Override
    public Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color) {
        return Optional.empty();
    }
}
