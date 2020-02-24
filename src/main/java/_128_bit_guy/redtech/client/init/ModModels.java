package _128_bit_guy.redtech.client.init;

import _128_bit_guy.redtech.client.model.ModModelVariantProvider;
import _128_bit_guy.redtech.client.model.SimpleUnbakedModel;
import _128_bit_guy.redtech.client.model.item.CoverModel;
import _128_bit_guy.redtech.client.model.item.WireModel;
import _128_bit_guy.redtech.client.model.part.CoverModelBaker;
import _128_bit_guy.redtech.client.model.part.WireModelBaker;
import _128_bit_guy.redtech.common.part.key.CoverModelKey;
import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.multipart.api.render.PartStaticModelRegisterEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

@Environment(EnvType.CLIENT)
public class ModModels {
    public static void init() {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(ModModelVariantProvider::new);
        PartStaticModelRegisterEvent.EVENT.register((renderer -> {
            renderer.register(CoverModelKey.class, new CoverModelBaker());
            renderer.register(WireModelKey.class, new WireModelBaker());
        }));
//        MultipartRenderRegistry.registerBaker(CoverModelKey.class, new CoverModelBaker());
//        MultipartRenderRegistry.registerBaker(WireModelKey.class, new WireModelBaker());
        ModModelVariantProvider.register("cover", "inventory", new SimpleUnbakedModel(new CoverModel()));
        ModModelVariantProvider.register("red_alloy_wire", "inventory", new SimpleUnbakedModel(new WireModel()));
    }
}
