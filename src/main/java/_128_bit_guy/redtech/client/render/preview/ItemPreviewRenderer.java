package _128_bit_guy.redtech.client.render.preview;

import _128_bit_guy.redtech.common.item.preview.PreviewRendererKey;

public interface ItemPreviewRenderer<T extends PreviewRendererKey> {
    void render(T key);
}
