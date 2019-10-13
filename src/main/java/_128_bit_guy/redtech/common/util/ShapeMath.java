package _128_bit_guy.redtech.common.util;


import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ShapeMath {
    private ShapeMath() {}

    public static VoxelShape intCube(int x0, int y0, int z0, int x1, int y1, int z1) {
        return VoxelShapes.cuboid(
            x0 / 16.0, y0 / 16.0, z0 / 16.0, //
            x1 / 16.0, y1 / 16.0, z1 / 16.0//
        );
    }

    public static VoxelShape vecCube(Vec3d a, Vec3d b) {
        return VoxelShapes.cuboid(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static VoxelShape rotate(VoxelShape shape, Direction from, Direction to) {
        return rotate(shape, from, to, null);
    }

    public static VoxelShape rotate(VoxelShape shape, Direction from, Direction to, Axis rotationAxis) {
        if (from == to) {
            return shape;
        }
        VoxelShape result = VoxelShapes.empty();
        for (Box box : shape.getBoundingBoxes()) {
            Vec3d min = VecMath.rotate(new Vec3d(box.minX, box.minY, box.minZ), from, to, rotationAxis);
            Vec3d max = VecMath.rotate(new Vec3d(box.maxX, box.maxY, box.maxZ), from, to, rotationAxis);
            result = VoxelShapes.combine(result, vecCube(min, max), BooleanBiFunction.OR);
        }
        return result.simplify();
    }
}
