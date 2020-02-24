package _128_bit_guy.redtech.common.item;

import _128_bit_guy.redtech.client.render.preview.CoverPreviewRenderer;
import _128_bit_guy.redtech.client.render.preview.ItemPreviewRenderer;
import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.init.ModParts;
import _128_bit_guy.redtech.common.item.preview.CoverPreviewKey;
import _128_bit_guy.redtech.common.item.preview.PreviewRendererProvider;
import _128_bit_guy.redtech.common.part.CoverPart;
import _128_bit_guy.redtech.common.util.VecMath;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
//import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class CoverItem extends Item implements PreviewRendererProvider<CoverPreviewKey> {
    public CoverItem() {
        super(new Settings());
    }

    public ItemStack createStack(int size, BlockState state) {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("size", size);
        tag.put("state", NbtHelper.fromBlockState(state));
        return stack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction side = ctx.getSide();
        ItemStack stack = ctx.getStack();
        Vec3d hitPos = ctx.getHitPos();
        int size = stack.getOrCreateTag().getInt("size");
        BlockState state = NbtHelper.toBlockState(stack.getOrCreateTag().getCompound("state"));
        if(world.isClient()) {
            return ActionResult.PASS;
        }
        Pair<BlockPos, Direction> p = getCoverDirection(world, pos, side, hitPos, size);
        if(p != null) {
            MultipartContainer.PartOffer offer = MultipartUtil.offerNewPart(world, p.getLeft(), h -> new CoverPart(ModParts.COVER, h, size, p.getRight(), state));
            if(offer != null) {
                offer.apply();
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    public Pair<BlockPos, Direction> getCoverDirection(World world, BlockPos pos, Direction side, Vec3d hitPos, int size) {
        Direction.Axis axis = side.getAxis();
        Vec3d blockCenter = VecMath.floorAll(hitPos).add(0.5, 0.5, 0.5);
        Vec3d diff = hitPos.subtract(blockCenter);
        diff = VecMath.setAxis(diff, axis, 0);
        final Direction side2 = VecMath.getMaxDirection(diff, 0.25, side);
        MultipartContainer.PartOffer offer = MultipartUtil.offerNewPart(world, pos, h -> new CoverPart(ModParts.COVER, h, size, side2, Blocks.STONE.getDefaultState()));
        if (offer != null) {
            return new Pair<>(pos, side2);
        }
        Direction side3 = VecMath.getMaxDirection(diff, 0.25, side.getOpposite());
        MultipartContainer.PartOffer offer2 = MultipartUtil.offerNewPart(world, pos.offset(side), h -> new CoverPart(ModParts.COVER, h, size, side3, Blocks.STONE.getDefaultState()));
        if (offer2 != null) {
            return new Pair<>(pos.offset(side), side3);
        }
        return null;
    }

    @Override
    public Text getName(ItemStack itemStack_1) {
        CompoundTag tag = itemStack_1.getOrCreateTag();
        String unlocalized = "item." + RedTech.ID + ".cover";
        unlocalized += "." + tag.getInt("size");
        return new TranslatableText(unlocalized, NbtHelper.toBlockState(tag.getCompound("state")).getBlock().getName());
    }

    public static void addSubItems(List<ItemStack> list) {
        CoverItem item = ModItems.COVER;
        Registry.BLOCK.forEach(b -> {
                    for (int i = 0; i < 7; ++i) {
                        list.add(item.createStack(i, b.getDefaultState()));
                    }
                }
        );
    }

    @Override
    public CoverPreviewKey instantiateKey() {
        return new CoverPreviewKey();
    }

    @Override
    public CoverPreviewKey populateKeyData(CoverPreviewKey key, ItemUsageContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction side = ctx.getSide();
        ItemStack stack = ctx.getStack();
        Vec3d hitPos = ctx.getHitPos();
        int size = stack.getOrCreateTag().getInt("size");
        BlockState state = NbtHelper.toBlockState(stack.getOrCreateTag().getCompound("state"));
        Pair<BlockPos, Direction> p = getCoverDirection(world, pos, side, hitPos, size);
        if(p == null) {
            return null;
        }
        key.direction = p.getRight();
        key.pos = p.getLeft();
        key.size = size;
        key.state = state;
        return key;
    }

    @Override
    public ItemPreviewRenderer<CoverPreviewKey> getRenderer() {
        return CoverPreviewRenderer.INSTANCE;
    }
}
