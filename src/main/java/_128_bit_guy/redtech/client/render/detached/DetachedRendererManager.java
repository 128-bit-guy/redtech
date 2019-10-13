/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package _128_bit_guy.redtech.client.render.detached;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * Dispatches "detached renderer elements" - rendering that does not require a specific block or entity in the world.
 */
@Environment(EnvType.CLIENT)
public enum DetachedRendererManager {
    INSTANCE;

    private final Map<RenderMatrixType, List<DetachedRenderer>> renders = new EnumMap<>(RenderMatrixType.class);

    DetachedRendererManager() {
        for (RenderMatrixType type : RenderMatrixType.values()) {
            renders.put(type, new ArrayList<>());
        }
    }

    public void addRenderer(RenderMatrixType type, DetachedRenderer renderer) {
        renders.get(type).add(renderer);
    }

    public void renderAfterCutout(PlayerEntity player, float partialTicks) {

        for (RenderMatrixType type : RenderMatrixType.values()) {
            List<DetachedRenderer> rendersForType = this.renders.get(type);
            if (rendersForType.isEmpty()) continue;
            type.glPre(player, partialTicks);
            for (DetachedRenderer render : rendersForType) {
                render.render(player, partialTicks);
            }
            type.glPost();
        }
    }

}
