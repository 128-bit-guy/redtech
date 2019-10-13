package _128_bit_guy.redtech.client.init;

import _128_bit_guy.redtech.client.model.ModModelVariantProvider;
import _128_bit_guy.redtech.client.model.SimpleUnbakedModel;
import _128_bit_guy.redtech.client.model.item.CoverModel;
import _128_bit_guy.redtech.client.model.part.CoverModelBaker;
import _128_bit_guy.redtech.common.part.key.CoverModelKey;
import alexiil.mc.lib.multipart.api.render.MultipartRenderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

@Environment(EnvType.CLIENT)
public class ModModels {
    public static void init() {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(ModModelVariantProvider::new);
        MultipartRenderRegistry.registerBaker(CoverModelKey.class, new CoverModelBaker());
        ModModelVariantProvider.register("cover", "inventory", new SimpleUnbakedModel(new CoverModel()));
    }
}
