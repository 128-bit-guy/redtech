package _128_bit_guy.redtech.client.render.detached;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface PostRender {
    void glPost();
}
