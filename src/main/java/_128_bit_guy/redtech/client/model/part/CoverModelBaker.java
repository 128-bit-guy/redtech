package _128_bit_guy.redtech.client.model.part;

import _128_bit_guy.redtech.common.part.key.CoverModelKey;
import _128_bit_guy.redtech.client.util.ModelUtils;
import _128_bit_guy.redtech.common.part.CoverPart;
import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CoverModelBaker implements PartModelBaker<CoverModelKey> {

    @Override
    public void emitQuads(CoverModelKey key, PartRenderContext ctx) {
        QuadEmitter emitter = ctx.getEmitter();
        Direction dir = key.direction;
        Sprite sprite = ModelUtils.getModel(key.state).getSprite();
        for(Box box : CoverPart.getCoverShape(key.size, key.direction).getBoundingBoxes()) {
//            Box box = CoverPart.getCoverShape(key.size, key.direction).getBoundingBox();
            ModelUtils.emitBoxQuads(box, emitter, q -> {
                if (key.culled.contains(q.nominalFace())) {
                    return false;
                }
                q.spriteBake(0, sprite, MutableQuadView.BAKE_ROTATE_90 | MutableQuadView.BAKE_FLIP_V | MutableQuadView.BAKE_FLIP_U);
                if (q.nominalFace() == dir || q.nominalFace().getAxis() != dir.getAxis()) {
                    q.cullFace(q.nominalFace());
                }
                return true;
            });
        }
    }
}
