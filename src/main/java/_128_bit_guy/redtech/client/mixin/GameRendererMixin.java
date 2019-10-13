package _128_bit_guy.redtech.client.mixin;

import _128_bit_guy.redtech.client.render.detached.DetachedRendererManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
        at = @At(value = "INVOKE", ordinal = 1, target = "Lcom/mojang/blaze3d/platform/GlStateManager;shadeModel(I)V"),
        method = "renderCenter(FJ)V")
    public void renderDetached(float f, long l, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;
        float partialTicks = mc.getTickDelta();
        if (mc.isPaused()) {
            partialTicks = 1;
        }

        DetachedRendererManager.INSTANCE.renderAfterCutout(player, partialTicks);
    }
}
