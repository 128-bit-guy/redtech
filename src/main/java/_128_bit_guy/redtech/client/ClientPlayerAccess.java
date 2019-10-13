package _128_bit_guy.redtech.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ClientPlayerAccess {
    public static PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }
}
