package _128_bit_guy.redtech.common.item;

import _128_bit_guy.redtech.common.init.ModParts;
import _128_bit_guy.redtech.common.init.ModTabs;
import _128_bit_guy.redtech.common.part.WirePart;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WireItem extends Item {
    public WireItem() {
        super(new Settings().group(ModTabs.WIRING));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        if (itemUsageContext_1.getWorld().isClient()) {
            return ActionResult.PASS;
        }
        Direction direction = itemUsageContext_1.getSide();
        BlockPos pos = itemUsageContext_1.getBlockPos().offset(direction);
        World world = itemUsageContext_1.getWorld();
        MultipartContainer.PartOffer offer = MultipartUtil.offerNewPart(world, pos, h -> new WirePart(ModParts.WIRE, h, direction.getOpposite()));
        if(offer != null) {
            offer.apply();
            itemUsageContext_1.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
