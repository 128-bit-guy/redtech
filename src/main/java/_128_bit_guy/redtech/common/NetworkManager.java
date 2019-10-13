package _128_bit_guy.redtech.common;

import _128_bit_guy.redtech.client.ClientPlayerAccess;
import _128_bit_guy.redtech.client.render.preview.ItemPreviewRenderer;
import _128_bit_guy.redtech.common.item.preview.PreviewRendererKey;
import _128_bit_guy.redtech.common.item.preview.PreviewRendererProvider;
import alexiil.mc.lib.net.*;
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil;
import alexiil.mc.lib.net.impl.McNetworkStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;

public class NetworkManager implements ServerTickCallback {
    public static ParentNetId RT_ROOT = McNetworkStack.ROOT.child(RedTech.ID);
    public static NetIdData ITEM_PREVIEW = RT_ROOT.idData("item_preview");

    @Environment(EnvType.CLIENT)
    public static PreviewRendererKey ITEM_PREVIEW_KEY;
    @Environment(EnvType.CLIENT)
    public static ItemPreviewRenderer ITEM_PREVIEW_RENDERER;

    static {
        ITEM_PREVIEW.setReceiver(NetworkManager::receiveItemPreview);
    }

    public static void receiveItemPreview(NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        ctx.assertClientSide();
        ITEM_PREVIEW_KEY = null;
        ITEM_PREVIEW_RENDERER = null;
        if(!buffer.readBoolean()) {
            return;
        }
        PlayerEntity player = ClientPlayerAccess.getPlayer();
        ItemStack stack = player.getMainHandStack();
        if(!(stack.getItem() instanceof PreviewRendererProvider)) {
            return;
        }
        PreviewRendererProvider it = (PreviewRendererProvider) stack.getItem();
        PreviewRendererKey key = it.instantiateKey();
        key.receive(buffer, ctx);
        ITEM_PREVIEW_KEY = key;
        ITEM_PREVIEW_RENDERER = it.getRenderer();
    }

    @SuppressWarnings("unchecked")
    public static void writeItemPreview(NetByteBuf buffer, IMsgWriteCtx ctx, ServerPlayerEntity player) {
        ctx.assertServerSide();
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof PreviewRendererProvider)) {
            buffer.writeBoolean(false);
            return;
        }
        Vec3d vec3d_1 = player.getCameraPosVec(1);
        Vec3d vec3d_2 = player.getRotationVec(1);
        Vec3d vec3d_3 = vec3d_1.add(vec3d_2.x * 5, vec3d_2.y * 5, vec3d_2.z * 5);
        BlockHitResult res = player.world.rayTrace(new RayTraceContext(vec3d_1, vec3d_3, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, player));
        if (res == null || res.getType() == HitResult.Type.MISS) {
            buffer.writeBoolean(false);
            return;
        }
        ItemUsageContext ctx2 = new ItemUsageContext(player, player.getActiveHand(), res);
        PreviewRendererProvider it = (PreviewRendererProvider) stack.getItem();
        PreviewRendererKey key = it.instantiateKey();
        key = it.populateKeyData(key, ctx2);
        if (key == null) {
            buffer.writeBoolean(false);
            return;
        }
        buffer.writeBoolean(true);
        key.write(buffer, ctx);
    }

    @Override
    public void tick(MinecraftServer server) {
        for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList()) {
            ITEM_PREVIEW.send(CoreMinecraftNetUtil.getConnection(playerEntity), (b, c) -> writeItemPreview(b, c, playerEntity));
        }
    }
}
