package _128_bit_guy.redtech.common.item;

import _128_bit_guy.redtech.common.init.ModTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class MultimeterItem extends Item {
    public MultimeterItem() {
        super(new Settings().group(ModTabs.MISC).maxCount(1));
    }
}
