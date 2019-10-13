package _128_bit_guy.redtech.client.util;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class ModelUtils {
    public static final int WHITE = 0xFFFFFFFF;

    public static void emitBoxQuads(Box box, QuadEmitter emitter, RenderContext.QuadTransform transform) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;
        {
            emitter
                    .pos(0, minX, minY, minZ)
                    .pos(3, minX, maxY, minZ)
                    .pos(2, minX, maxY, maxZ)
                    .pos(1, minX, minY, maxZ)
                    .nominalFace(Direction.WEST)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minY * 16, minZ * 16)
                    .sprite(3, 0, maxY * 16, minZ * 16)
                    .sprite(2, 0, maxY * 16, maxZ * 16)
                    .sprite(1, 0, minY * 16, maxZ * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
        {
            emitter
                    .pos(0, maxX, minY, minZ)
                    .pos(1, maxX, maxY, minZ)
                    .pos(2, maxX, maxY, maxZ)
                    .pos(3, maxX, minY, maxZ)
                    .nominalFace(Direction.EAST)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minY * 16, minZ * 16)
                    .sprite(1, 0, maxY * 16, minZ * 16)
                    .sprite(2, 0, maxY * 16, maxZ * 16)
                    .sprite(3, 0, minY * 16, maxZ * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
        {
            emitter
                    .pos(0, minX, maxY, minZ)
                    .pos(3, maxX, maxY, minZ)
                    .pos(2, maxX, maxY, maxZ)
                    .pos(1, minX, maxY, maxZ)
                    .nominalFace(Direction.UP)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minX * 16, minZ * 16)
                    .sprite(3, 0, maxX * 16, minZ * 16)
                    .sprite(2, 0, maxX * 16, maxZ * 16)
                    .sprite(1, 0, minX * 16, maxZ * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
        {
            emitter
                    .pos(0, minX, minY, minZ)
                    .pos(1, maxX, minY, minZ)
                    .pos(2, maxX, minY, maxZ)
                    .pos(3, minX, minY, maxZ)
                    .nominalFace(Direction.DOWN)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minX * 16, minZ * 16)
                    .sprite(1, 0, maxX * 16, minZ * 16)
                    .sprite(2, 0, maxX * 16, maxZ * 16)
                    .sprite(3, 0, minX * 16, maxZ * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
        {
            emitter
                    .pos(0, minX, minY, minZ)
                    .pos(1, minX, maxY, minZ)
                    .pos(2, maxX, maxY, minZ)
                    .pos(3, maxX, minY, minZ)
                    .nominalFace(Direction.NORTH)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minY * 16, minX * 16)
                    .sprite(1, 0, maxY * 16, minX * 16)
                    .sprite(2, 0, maxY * 16, maxX * 16)
                    .sprite(3, 0, minY * 16, maxX * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
        {
            emitter
                    .pos(0, minX, minY, maxZ)
                    .pos(3, minX, maxY, maxZ)
                    .pos(2, maxX, maxY, maxZ)
                    .pos(1, maxX, minY, maxZ)
                    .nominalFace(Direction.SOUTH)
                    .spriteColor(0, WHITE, WHITE, WHITE, WHITE)
                    .sprite(0, 0, minY * 16, minX * 16)
                    .sprite(3, 0, maxY * 16, minX * 16)
                    .sprite(2, 0, maxY * 16, maxX * 16)
                    .sprite(1, 0, minY * 16, maxX * 16);
            if (transform.transform(emitter)) {
                emitter
                        .emit();
            }
        }
    }

    public static BakedModel getModel(BlockState state) {
        return MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(state);
    }
}
