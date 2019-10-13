package _128_bit_guy.redtech.common.item.preview;

import alexiil.mc.lib.net.NetIdData;

public interface PreviewRendererKey extends NetIdData.IMsgDataReceiver, NetIdData.IMsgDataWriter {
    boolean equals(PreviewRendererKey k);

}
