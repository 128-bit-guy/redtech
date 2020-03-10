package _128_bit_guy.redtech.client;

import _128_bit_guy.redtech.client.init.ModModels;
import _128_bit_guy.redtech.client.init.ModSprites;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RedTechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModModels.init();
        ModSprites.init();
    }
}
