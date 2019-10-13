package _128_bit_guy.redtech.common.item;

import _128_bit_guy.redtech.common.init.ModTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class SawItem extends Item implements AdvancedRecipeRemainderProvider {
    public final SawMaterial material;
    public SawItem(SawMaterial material) {
        super(new Settings().maxCount(1).maxDamage(material.damage).group(ModTabs.MISC));
        this.material = material;
    }

    @Override
    public ItemStack getRecipeRemainderAdv(ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.damage(1, new Random(), null);
        if(newStack.getDamage() >= newStack.getMaxDamage()) {
            return ItemStack.EMPTY;
        } else {
            return newStack;
        }
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }
}
