package _128_bit_guy.redtech.common.recipe.serializer;

import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.init.ModRecipeSerializers;
import _128_bit_guy.redtech.common.item.AdvancedRecipeRemainderProvider;
import _128_bit_guy.redtech.common.item.SawItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CoverCreationRecipe extends SpecialCraftingRecipe {
    public CoverCreationRecipe(Identifier identifier_1) {
        super(identifier_1);
    }

    @Override
    public boolean matches(CraftingInventory var1, World var2) {
        for (int x = 0; x < var1.getWidth(); ++x) {
            for (int y = 0; y < var1.getHeight() - 1; ++y) {
                int slot0 = x + y * var1.getWidth();
                int slot1 = x + (y + 1) * var1.getWidth();
                if (var1.getInvStack(slot0).getItem() instanceof SawItem) {
                    if (var1.getInvStack(slot1).getItem() instanceof BlockItem) {
                        return true;
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
                    if (var1.getInvStack(slot1).getItem() instanceof BlockItem) {
                        ItemStack inSlot1 = var1.getInvStack(slot1);
                        BlockState state = ((BlockItem) inSlot1.getItem()).getBlock().getDefaultState();
                        ItemStack stack = ModItems.COVER.createStack(3, state);
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


    @Override
    public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList_1 = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

        for (int int_1 = 0; int_1 < defaultedList_1.size(); ++int_1) {
            Item item_1 = inventory.getInvStack(int_1).getItem();
            if (item_1.hasRecipeRemainder()) {
                if (item_1 instanceof AdvancedRecipeRemainderProvider) {
                    defaultedList_1.set(int_1, ((AdvancedRecipeRemainderProvider) item_1).getRecipeRemainderAdv(inventory.getInvStack(int_1)));
                } else {
                    defaultedList_1.set(int_1, new ItemStack(item_1.getRecipeRemainder()));
                }
            }
        }

        return defaultedList_1;
    }
}
