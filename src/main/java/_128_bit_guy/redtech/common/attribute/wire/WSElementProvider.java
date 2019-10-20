package _128_bit_guy.redtech.common.attribute.wire;

import _128_bit_guy.redtech.common.part.wire.WirePointer;
import alexiil.mc.lib.attributes.AttributeCombiner;
import alexiil.mc.lib.attributes.Attributes;
import alexiil.mc.lib.attributes.CombinableAttribute;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public interface WSElementProvider {
    CombinableAttribute<WSElementProvider> ATTRIBUTE =
            Attributes.createCombinable(
                    WSElementProvider.class,
                    EmptyWSElementProvider.INSTANCE,
                    (AttributeCombiner<WSElementProvider>) CombinedWSElementProvider::new
            );

    Optional<WSElement> get(Direction mainDirection, Direction searchDirection, WSElementType type, DyeColor color);

    static Optional<WSElement> getFromPtr(WirePointer ptr, WSElementType type, DyeColor color) {
        WSElementProvider provider = ATTRIBUTE.get(ptr.getWorld(), ptr.getBlockPos());
        return provider.get(ptr.mainDirection, ptr.direction, type, color);
    }
}
