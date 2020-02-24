package _128_bit_guy.redtech.client.render.preview;

import _128_bit_guy.redtech.common.item.preview.CoverPreviewKey;
import _128_bit_guy.redtech.common.part.key.CoverModelKey;
import _128_bit_guy.redtech.common.util.VecMath;
import alexiil.mc.lib.multipart.impl.LibMultiPart;
import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.*;

import java.util.Collections;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public enum CoverPreviewRenderer implements ItemPreviewRenderer<CoverPreviewKey> {
    INSTANCE;

    @Override
    public void render(CoverPreviewKey key) {
        //Cover preview
//        {
//            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
//            MinecraftClient.getInstance().gameRenderer.enableLightmap();
//
//            CoverModelKey mk = new CoverModelKey(key.direction, key.size, key.state, Collections.emptySet());
//            SinglePartBakedModel<?> model = SinglePartBakedModel.create(mk);
//            Tessellator t = Tessellator.getInstance();
//            BufferBuilder bb = t.getBuffer();
//
//            bb.begin(GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);
//            MinecraftClient mc = MinecraftClient.getInstance();
//            BlockModelRenderer blockRenderer = mc.getBlockRenderManager().getModelRenderer();
//
//            blockRenderer.render(
//                    mc.world, model, LibMultiPart.BLOCK.getDefaultState(), key.pos, new MatrixStack(), bb, false, new Random(), 0, 0
//            );
//
//            bb.setOffset(0, 0, 0);
//            GlStateManager.enableBlend();
//            GlStateManager.blendFuncSeparate(
//                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.DestFactor.ONE
//            );
//            GL14.glBlendColor(1, 1, 1, 0.5f);
//            GL11.glDepthRange(0, 0.5);
//            t.draw();
//            GL11.glDepthRange(0, 1);
//            GL14.glBlendColor(0, 0, 0, 0);
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//            GlStateManager.disableBlend();
//
//            MinecraftClient.getInstance().gameRenderer.disableLightmap();
//        }
//
//        //Lines
//        {
//            HitResult hr = MinecraftClient.getInstance().hitResult;
//            if (hr.getType() == HitResult.Type.BLOCK) {
//                BlockHitResult bhr = (BlockHitResult) hr;
//                Vec3d v = bhr.getPos();
//                double x = v.getComponentAlongAxis(bhr.getSide().getAxis()) + bhr.getSide().getDirection().offset() * 0.02d;
//                v = VecMath.floorAll(v);
//                v = VecMath.setAxis(v, bhr.getSide().getAxis(), x);
//                Direction.Axis axis0 = VecMath.nextAxis(bhr.getSide().getAxis());
//                Direction.Axis axis1 = VecMath.nextAxis(axis0);
//                Vec3d v00 = v;
//                Vec3d v10 = v00.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis0, 1));
//                Vec3d v11 = v10.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis1, 1));
//                Vec3d v01 = v00.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis1, 1));
//                Vec3d v00i = v.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis0, 0.25)).add(VecMath.setAxis(new Vec3d(0, 0, 0), axis1, 0.25));
//                Vec3d v10i = v00i.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis0, 0.5));
//                Vec3d v11i = v10i.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis1, 0.5));
//                Vec3d v01i = v00i.add(VecMath.setAxis(new Vec3d(0, 0, 0), axis1, 0.5));
//                GlStateManager.disableDepthTest();
//                GlStateManager.disableTexture();
//                Tessellator t = Tessellator.getInstance();
//                BufferBuilder bb = t.getBufferBuilder();
//                bb.begin(GL_LINES, VertexFormats.POSITION_COLOR);
//                bb.vertex(v00.x, v00.y, v00.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v00i.x, v00i.y, v00i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10.x, v10.y, v10.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10i.x, v10i.y, v10i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11.x, v11.y, v11.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11i.x, v11i.y, v11i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01.x, v01.y, v01.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01i.x, v01i.y, v01i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v00i.x, v00i.y, v00i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01i.x, v01i.y, v01i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01i.x, v01i.y, v01i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11i.x, v11i.y, v11i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11i.x, v11i.y, v11i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10i.x, v10i.y, v10i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10i.x, v10i.y, v10i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v00i.x, v00i.y, v00i.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//
//                bb.vertex(v00.x, v00.y, v00.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01.x, v01.y, v01.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v01.x, v01.y, v01.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11.x, v11.y, v11.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v11.x, v11.y, v11.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10.x, v10.y, v10.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v10.x, v10.y, v10.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//                bb.vertex(v00.x, v00.y, v00.z).color(0.0f, 0.0f, 0.0f, 1.0f).next();
//
//                t.draw();
//                GlStateManager.enableDepthTest();
//                GlStateManager.enableTexture();
//            }
//        }
    }
}
