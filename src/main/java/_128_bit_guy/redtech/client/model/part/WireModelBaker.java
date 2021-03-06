package _128_bit_guy.redtech.client.model.part;

import _128_bit_guy.redtech.client.init.ModModels;
import _128_bit_guy.redtech.client.init.ModSprites;
import _128_bit_guy.redtech.client.util.ModelUtils;
import _128_bit_guy.redtech.common.part.wire.RedAlloyWirePart;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class WireModelBaker implements PartModelBaker<WireModelKey> {
    @Override
    public void emitQuads(WireModelKey key, PartRenderContext ctx) {
        Sprite sprite = ModSprites.getBlockSprite(ModSprites.RED_ALLOY_WIRE);
        QuadEmitter emitter = ctx.getEmitter();
        Direction dir = key.mainDirection;
        for (Box box : RedAlloyWirePart.getWireShape(dir, key.connections).getBoundingBoxes()) {
            ModelUtils.emitBoxQuads(box, emitter, q -> {
                q.spriteBake(0, sprite, MutableQuadView.BAKE_ROTATE_90 | MutableQuadView.BAKE_FLIP_V | MutableQuadView.BAKE_FLIP_U);
                if (q.nominalFace() == dir) {
                    q.cullFace(q.nominalFace());
                }
                int allColor = key.power << (4 * 4);
                allColor |= 0xFF000000;
                q.spriteColor(0, allColor, allColor, allColor, allColor);
                if(key.power >= 10) {
                    q.material(ModModels.SOLID_EMISSIVE);
                } else {
                    q.material(ModModels.SOLID_NORMAL);
                }
                return true;
            });
        }
    }

}
