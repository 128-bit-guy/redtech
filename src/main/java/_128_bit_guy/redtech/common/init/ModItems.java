package _128_bit_guy.redtech.common.init;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.item.CoverItem;
import _128_bit_guy.redtech.common.item.SawItem;
import _128_bit_guy.redtech.common.item.SawMaterial;
import _128_bit_guy.redtech.common.item.WireItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static CoverItem COVER;
    public static Map<SawMaterial, SawItem> SAWS = new EnumMap<>(SawMaterial.class);
    public static WireItem WIRE;

    public static void init() {
        COVER = register("cover", new CoverItem());
        for(SawMaterial material : SawMaterial.values()) {
            SAWS.put(material, register(material.toString().toLowerCase() + "_saw", new SawItem(material)));
        }
        WIRE = register("red_alloy_wire", new WireItem());
    }

    private static  <T extends Item> T register(String id, T entry) {
        return Registry.register(Registry.ITEM, new Identifier(RedTech.ID, id), entry);
    }
}
