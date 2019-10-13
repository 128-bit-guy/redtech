package _128_bit_guy.redtech.client.model.item;

import _128_bit_guy.redtech.client.util.ModelUtils;
import _128_bit_guy.redtech.common.part.CoverPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.SpriteFinderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class CoverModel implements BakedModel, FabricBakedModel {
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {

    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        int size = stack.getOrCreateTag().getInt("size");
        BlockState state = TagHelper.deserializeBlockState(stack.getTag().getCompound("state"));
        Box box = CoverPart.getCoverShape(size, Direction.NORTH).getBoundingBox();
        Sprite sprite = ModelUtils.getModel(state).getSprite();
        ModelUtils.emitBoxQuads(box, context.getEmitter(), q -> {
            q.spriteBake(0, sprite, MutableQuadView.BAKE_ROTATE_90 | MutableQuadView.BAKE_FLIP_V | MutableQuadView.BAKE_FLIP_U);
            return true;
        });
    }

    @Override
    public List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepthInGui() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return MissingSprite.getMissingSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        Transformation transformation = new Transformation(new Vector3f(20f, 20f, 20f), new Vector3f(), new Vector3f(0.7f, 0.7f, 0.7f));
        return new ModelTransformation(transformation, transformation, transformation, transformation, transformation, transformation, transformation, transformation);
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return ModelItemPropertyOverrideList.EMPTY;
    }
}
