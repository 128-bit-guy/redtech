package _128_bit_guy.redtech.client.init;

import _128_bit_guy.redtech.client.render.detached.DetachedRendererManager;
import _128_bit_guy.redtech.client.render.preview.ItemPreviewRenderManager;
import _128_bit_guy.redtech.client.render.detached.RenderMatrixType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModRenderers {
    public static void init() {
        DetachedRendererManager.INSTANCE.addRenderer(RenderMatrixType.FROM_WORLD_ORIGIN, new ItemPreviewRenderManager());
    }
}
