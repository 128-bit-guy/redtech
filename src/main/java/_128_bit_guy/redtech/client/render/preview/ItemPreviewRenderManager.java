package _128_bit_guy.redtech.client.render.preview;

import _128_bit_guy.redtech.client.render.detached.DetachedRenderer;
import _128_bit_guy.redtech.common.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class ItemPreviewRenderManager implements DetachedRenderer {
    @SuppressWarnings("unchecked")
    @Override
    public void render(PlayerEntity player, float partialTicks) {
        if(NetworkManager.ITEM_PREVIEW_RENDERER != null) {
            NetworkManager.ITEM_PREVIEW_RENDERER.render(NetworkManager.ITEM_PREVIEW_KEY);
        }
    }
}
