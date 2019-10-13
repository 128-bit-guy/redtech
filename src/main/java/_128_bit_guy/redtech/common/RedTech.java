package _128_bit_guy.redtech.common;

import _128_bit_guy.redtech.common.init.ModItems;
import _128_bit_guy.redtech.common.init.ModParts;
import _128_bit_guy.redtech.common.init.ModRecipeSerializers;
import _128_bit_guy.redtech.common.init.ModTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;

public class RedTech implements ModInitializer {
    public static final String ID = "redtech";
    @Override
    public void onInitialize() {
        ModTabs.init();
        ModParts.init();
        ModItems.init();
        ModRecipeSerializers.init();
        ServerTickCallback.EVENT.register(new NetworkManager());
    }
}
