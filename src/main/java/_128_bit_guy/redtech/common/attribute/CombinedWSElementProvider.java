package _128_bit_guy.redtech.common.attribute;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Optional;

public class CombinedWSElementProvider implements WSElementProvider {
    private final List<? extends WSElementProvider> list;

    public CombinedWSElementProvider(List<? extends WSElementProvider> list) {
        this.list = list;
    }

    @Override
    public Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color) {
        Optional<Optional<WSElement>> r = list
                .stream()
                .map(e -> e.get(mainDirection, searchDirection, type, color))
                .filter(Optional::isPresent)
                .findAny();
        return r.orElse(Optional.empty());
    }
}
