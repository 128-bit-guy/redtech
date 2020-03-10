package _128_bit_guy.redtech.common.init;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.item.*;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static CoverItem COVER;
    public static Map<SawMaterial, SawItem> SAWS = new EnumMap<>(SawMaterial.class);
    public static RedAlloyWireItem RED_ALLOY_WIRE;
    public static Map<DyeColor, RedAlloyWireItem> COLORED_RED_ALLOY_WIRES = new EnumMap<>(DyeColor.class);
    public static MultimeterItem MULTIMETER;

    public static void init() {
        COVER = register("cover", new CoverItem());
        for(SawMaterial material : SawMaterial.values()) {
            SAWS.put(material, register(material.toString().toLowerCase() + "_saw", new SawItem(material)));
        }
        RED_ALLOY_WIRE = register("red_alloy_wire", new RedAlloyWireItem(null));
        for(DyeColor dyeColor : DyeColor.values()) {
            COLORED_RED_ALLOY_WIRES.put(dyeColor, register(dyeColor.toString() + "_red_alloy_wire", new RedAlloyWireItem(dyeColor)));
        }
        MULTIMETER = register("multimeter", new MultimeterItem());
    }

    private static  <T extends Item> T register(String id, T entry) {
        return Registry.register(Registry.ITEM, new Identifier(RedTech.ID, id), entry);
    }
}
