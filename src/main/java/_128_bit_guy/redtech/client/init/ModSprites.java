package _128_bit_guy.redtech.client.init;

import _128_bit_guy.redtech.common.RedTech;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class ModSprites {
    public static Identifier RED_ALLOY_WIRE = new Identifier(RedTech.ID, "part/red_alloy_wire");

    public static void init() {
        ClientSpriteRegistryCallback
                .event(SpriteAtlasTexture.BLOCK_ATLAS_TEX)
                .register(ModSprites::registerSprites);
    }

    public static Sprite getBlockSprite(Identifier id) {
        return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(id);
    }

    private static void registerSprites(SpriteAtlasTexture texture, ClientSpriteRegistryCallback.Registry registry) {
        registry.register(RED_ALLOY_WIRE);
    }
}
