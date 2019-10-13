package _128_bit_guy.redtech.client.render.detached;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface PreRender {
    void glPre(PlayerEntity clientPlayer, float partialTicks);
}
