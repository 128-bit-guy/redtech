package _128_bit_guy.redtech.common.attribute;

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
}
