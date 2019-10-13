package _128_bit_guy.redtech.common.mixin;

import _128_bit_guy.redtech.common.item.AdvancedRecipeRemainderProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Recipe.class)
public interface RecipeMixin {
    /**
     * @author 128_bit_guy
     */
    @Overwrite
    default DefaultedList<ItemStack> getRemainingStacks(Inventory inventory_1) {
        DefaultedList<ItemStack> defaultedList_1 = DefaultedList.ofSize(inventory_1.getInvSize(), ItemStack.EMPTY);

        for (int int_1 = 0; int_1 < defaultedList_1.size(); ++int_1) {
            Item item_1 = inventory_1.getInvStack(int_1).getItem();
            if (item_1.hasRecipeRemainder()) {
                if (item_1 instanceof AdvancedRecipeRemainderProvider) {
                    defaultedList_1.set(int_1, ((AdvancedRecipeRemainderProvider) item_1).getRecipeRemainderAdv(inventory_1.getInvStack(int_1)));
                } else {
                    defaultedList_1.set(int_1, new ItemStack(item_1.getRecipeRemainder()));
                }
            }
        }

        return defaultedList_1;
    }
}
