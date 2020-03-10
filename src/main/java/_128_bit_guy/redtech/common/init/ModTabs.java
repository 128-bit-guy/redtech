package _128_bit_guy.redtech.common.init;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.item.CoverItem;
import _128_bit_guy.redtech.common.item.SawMaterial;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModTabs {
    public static ItemGroup MICROBLOCKS;
    public static ItemGroup WIRING;
    public static ItemGroup MISC;

    public static void init() {
        MICROBLOCKS = FabricItemGroupBuilder
                .create(new Identifier(RedTech.ID, "microblocks"))
                .icon(() -> ModItems.COVER.createStack(1, Blocks.STONE.getDefaultState()))
                .appendItems(CoverItem::addSubItems)
                .build();
        WIRING = FabricItemGroupBuilder
                .create(new Identifier(RedTech.ID, "wiring"))
                .icon(() -> new ItemStack(ModItems.RED_ALLOY_WIRE))
                .build();
        MISC = FabricItemGroupBuilder
                .create(new Identifier(RedTech.ID, "misc"))
                .icon(() -> new ItemStack(ModItems.SAWS.get(SawMaterial.DIAMOND)))
                .build();
    }
}
