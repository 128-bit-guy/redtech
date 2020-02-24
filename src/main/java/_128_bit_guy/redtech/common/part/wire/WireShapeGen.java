package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.util.ShapeMath;
import _128_bit_guy.redtech.common.util.VecMath;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WireShapeGen {
    public static void createWireShapes(double wireWidth, double wireHeight, VoxelShape[] centerShapes, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] notConnectedShapes) {
        double halfWidth = wireWidth / 2;
        {
            VoxelShape down = VoxelShapes.cuboid(0.5 - halfWidth, 0, 0.5 - halfWidth, 0.5 + halfWidth, wireHeight, 0.5 + halfWidth);
            for (Direction direction : Direction.values()) {
                centerShapes[direction.ordinal()] = ShapeMath.rotate(down, Direction.DOWN, direction);
            }
        }
        {
            VoxelShape down = VoxelShapes.cuboid(0.5 - 3 * halfWidth, 0, 0.5 - halfWidth, 0.5 + 3 * halfWidth, wireHeight, 0.5 + halfWidth);
            for (Direction direction : Direction.values()) {
                notConnectedShapes[direction.ordinal()] = ShapeMath.rotate(down, Direction.DOWN, direction);
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

    public static VoxelShape getWireCenterShape(Direction direction, VoxelShape[] centerShapes) {
        return centerShapes[direction.ordinal()];
    }

    public static VoxelShape getWireShape(Direction mainDirection, Set<Direction> connections, VoxelShape[] centerShapes, Map<Direction, Map<Direction, VoxelShape>> connectionShapes, VoxelShape[] notConnectedShapes) {
        if(connections.isEmpty()) {
            return notConnectedShapes[mainDirection.ordinal()];
        }
        VoxelShape r = getWireCenterShape(mainDirection, centerShapes);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != mainDirection.getAxis()) {
                if (connections.contains(direction)) {
                    VoxelShape s2 = connectionShapes.get(mainDirection).get(direction);
                    r = VoxelShapes.combine(r, s2, BooleanBiFunction.OR);
                }
            }
        }
        r = r.simplify();
        return r;
    }

    public static Set<Direction> mapToSet(Map<Direction, Boolean> connected) {
        return connected.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
