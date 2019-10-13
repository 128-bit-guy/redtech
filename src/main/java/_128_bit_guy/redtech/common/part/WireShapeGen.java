package _128_bit_guy.redtech.common.part;

import _128_bit_guy.redtech.common.util.ShapeMath;
import _128_bit_guy.redtech.common.util.VecMath;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.EnumMap;
import java.util.Map;

public class WireShapeGen {
    public static void createWireShapes(double wireWidth, double wireHeight, VoxelShape[] centerShapes, Map<Direction, Map<Direction, VoxelShape>> connectionShapes) {
        double halfWidth = wireWidth / 2;
        {
            VoxelShape down = VoxelShapes.cuboid(0.5 - halfWidth, 0, 0.5 - halfWidth, 0.5 + halfWidth, wireHeight, 0.5 + halfWidth);
            for (Direction direction : Direction.values()) {
                centerShapes[direction.ordinal()] = ShapeMath.rotate(down, Direction.DOWN, direction);
            }
        }
        {
            for(Direction direction : Direction.values()) {
                connectionShapes.put(direction, new EnumMap<>(Direction.class));
            }
            VoxelShape downWest = VoxelShapes.cuboid(0, 0, 0.5 - halfWidth, 0.5 - halfWidth, wireHeight, 0.5 + halfWidth);
            for (Direction horizontalDirection : Direction.values()) {
                if (horizontalDirection.getAxis() == Direction.Axis.Y) {
                    continue;
                }
                VoxelShape down = ShapeMath.rotate(downWest, Direction.WEST, horizontalDirection, Direction.Axis.Y);
                for(Direction mainDirection : Direction.values()) {
                    Direction addDirection = VecMath.rotateDirection(horizontalDirection, Direction.DOWN, mainDirection);
                    VoxelShape sh = ShapeMath.rotate(down, Direction.DOWN, mainDirection);
                    connectionShapes.get(mainDirection).put(addDirection, sh);
                }
            }
        }
    }
}
