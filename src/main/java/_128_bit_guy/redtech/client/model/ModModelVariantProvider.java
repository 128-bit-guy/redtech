package _128_bit_guy.redtech.client.model;

import _128_bit_guy.redtech.common.RedTech;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModModelVariantProvider implements ModelVariantProvider {
    public static Map<ModelIdentifier, UnbakedModel> REGISTRY = new HashMap<>();

    public ModModelVariantProvider(ResourceManager manager) {

    }

    public static void register(String id, String variant, UnbakedModel model) {
        REGISTRY.put(new ModelIdentifier(new Identifier(RedTech.ID, id), variant), model);
    }

    @Override
    public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        if (REGISTRY.containsKey(modelId)) {
            return REGISTRY.get(modelId);
        }
        return null;
    }
}
