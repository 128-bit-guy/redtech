package _128_bit_guy.redtech.common.init;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.part.CoverPart;
import _128_bit_guy.redtech.common.part.WirePart;
import alexiil.mc.lib.multipart.api.PartDefinition;
import net.minecraft.util.Identifier;

public class ModParts {
    public static PartDefinition COVER;
    public static PartDefinition WIRE;

    public static void init() {
        COVER = createAndRegister("cover", CoverPart::new, CoverPart::new);
        WIRE = createAndRegister("wire", WirePart::new, WirePart::new);
    }

    private static PartDefinition createAndRegister(String id, PartDefinition.IPartNbtReader reader, PartDefinition.IPartNetLoader loader) {
        Identifier partId = new Identifier(RedTech.ID, id);
        PartDefinition def = new PartDefinition(partId, reader, loader);
        PartDefinition.PARTS.put(partId, def);
        return def;
    }
}
