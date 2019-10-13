package _128_bit_guy.redtech.client.render.detached;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;

import static org.lwjgl.opengl.GL11.*;

@Environment(EnvType.CLIENT)
public enum RenderMatrixType implements PreRender, PostRender {
    FROM_PLAYER(null, null),
    FROM_WORLD_ORIGIN(RenderMatrixType::fromWorldOriginPre, RenderMatrixType::fromWorldOriginPost);

    public final PreRender pre;
    public final PostRender post;

    RenderMatrixType(PreRender pre, PostRender post) {
        this.pre = pre;
        this.post = post;
    }

    public static void fromWorldOriginPre(PlayerEntity player, float partialTicks) {
        glPushMatrix();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        glTranslated(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
    }

    public static void fromWorldOriginPost() {
        glPopMatrix();
    }

    @Override
    public void glPre(PlayerEntity clientPlayer, float partialTicks) {
        if (pre != null) pre.glPre(clientPlayer, partialTicks);
    }

    @Override
    public void glPost() {
        if (post != null) post.glPost();
    }

    public void addRenderer(DetachedRenderer renderer) {
        DetachedRendererManager.INSTANCE.addRenderer(this, renderer);
    }

}
