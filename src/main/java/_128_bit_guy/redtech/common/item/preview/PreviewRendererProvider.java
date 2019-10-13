package _128_bit_guy.redtech.common.item.preview;

import _128_bit_guy.redtech.client.render.preview.ItemPreviewRenderer;
import net.minecraft.item.ItemUsageContext;

public interface PreviewRendererProvider<T extends PreviewRendererKey> {
    /**
     * Simply instantiates key without any data. May be called on both client and server.
     * @return preview key without any data
     */
    T instantiateKey();

    /**
     * Populates key data from context. Called on server.
     * @param key preview key to populate
     */
    T populateKeyData(T key, ItemUsageContext context);

    ItemPreviewRenderer<T> getRenderer();
}
