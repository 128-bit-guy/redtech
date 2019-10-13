package _128_bit_guy.redtech.common.item;

import net.minecraft.item.ToolMaterials;

public enum SawMaterial {
    WOOD(32, ToolMaterials.WOOD), STONE(64, ToolMaterials.STONE), IRON(128, ToolMaterials.IRON), DIAMOND(256, ToolMaterials.DIAMOND);
    public final int damage;
    public final ToolMaterials toolMaterial;

    SawMaterial(int damage, ToolMaterials toolMaterial) {
        this.damage = damage;
        this.toolMaterial = toolMaterial;
    }
}
