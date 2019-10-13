package _128_bit_guy.redtech.common.recipe.serializer;

import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.init.ModRecipeSerializers;
import _128_bit_guy.redtech.common.item.CoverItem;
import _128_bit_guy.redtech.common.item.SawItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.world.World;

public class CoverSplitRecipe extends SpecialCraftingRecipe {
    public CoverSplitRecipe(Identifier identifier_1) {
        super(identifier_1);
    }

    @Override
    public boolean matches(CraftingInventory var1, World var2) {
        for (int x = 0; x < var1.getWidth(); ++x) {
            for (int y = 0; y < var1.getHeight() - 1; ++y) {
                int slot0 = x + y * var1.getWidth();
                int slot1 = x + (y + 1) * var1.getWidth();
                if (var1.getInvStack(slot0).getItem() instanceof SawItem) {
                    if (var1.getInvStack(slot1).getItem() instanceof CoverItem) {
                        int size = var1.getInvStack(slot1).getOrCreateTag().getInt("size");
                        return size > 0 && size % 2 != 0;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory var1) {
        for (int x = 0; x < var1.getWidth(); ++x) {
            for (int y = 0; y < var1.getHeight() - 1; ++y) {
                int slot0 = x + y * var1.getWidth();
                int slot1 = x + (y + 1) * var1.getWidth();
                if (var1.getInvStack(slot0).getItem() instanceof SawItem) {
                    if (var1.getInvStack(slot1).getItem() instanceof CoverItem) {
                        ItemStack inSlot1 = var1.getInvStack(slot1);
                        BlockState state = TagHelper.deserializeBlockState(inSlot1.getOrCreateTag().getCompound("state"));
                        int size = inSlot1.getOrCreateTag().getInt("size");
                        ItemStack stack = ModItems.COVER.createStack((size + 1) / 2 - 1, state);
                        stack.setCount(2);
                        return stack;
                    }
                }
            }
        }
        return ModItems.COVER.createStack(3, Blocks.STONE.getDefaultState());
    }

    @Override
    public boolean fits(int w, int h) {
        return w >= 1 && h >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.COVER_CREATION;
    }
}
