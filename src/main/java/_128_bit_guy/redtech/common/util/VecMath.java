package _128_bit_guy.redtech.common.util;

import net.fabricmc.loader.util.sat4j.core.Vec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VecMath {
    private VecMath() {
    }

    public static Vec3d rotate(Vec3d vec, Direction from, Direction to) {
        return rotate(vec, from, to, null, 0.5, 0.5, 0.5);
    }

    public static Vec3d rotate(Vec3d vec, Direction from, Direction to, Axis axis) {
        return rotate(vec, from, to, axis, 0.5, 0.5, 0.5);
    }

    public static Vec3d rotate(Vec3d vec, Direction from, Direction to, double ox, double oy, double oz) {
        return rotate(vec, from, to, null, ox, oy, oz);
    }

    public static Vec3d rotate(Vec3d vec, Direction from, Direction to, Axis axis, double ox, double oy,
                               double oz) {
        if (from == to) {
            return vec;
        }

        double x = vec.x - ox;
        double y = vec.y - oy;
        double z = vec.z - oz;

        switch (from.getAxis()) {
            case X: {
                int mult = from.getOffsetX();
                switch (to.getAxis()) {
                    case X: {
                        if (axis != Axis.Y) {
                            x = -x;
                            y = -y;
                        } else {
                            x = -x;
                            z = -z;
                        }
                        break;
                    }
                    case Y:
                        double zm = mult * to.getOffsetY();
                        double y0 = x * zm;
                        x = y * -zm;
                        y = y0;
                        break;
                    case Z:
                        float xm = mult * to.getOffsetZ();
                        double z0 = x * xm;
                        x = z * -xm;
                        z = z0;
                        break;
                }
                break;
            }
            case Y: {
                int mult = from.getOffsetY();
                switch (to.getAxis()) {
                    case X:
                        double xm = mult * to.getOffsetX();
                        double y0 = x * -xm;
                        x = y * xm;
                        y = y0;
                        break;
                    case Y: {
                        if (axis != Axis.Z) {
                            y = -y;
                            z = -z;
                        } else {
                            x = -x;
                            y = -y;
                        }
                        break;
                    }
                    case Z:
                        double ym = mult * to.getOffsetZ();
                        double z0 = y * ym;
                        y = z * -ym;
                        z = z0;
                        break;
                }
                break;
            }
            case Z: {
                int mult = -from.getOffsetZ();
                switch (to.getAxis()) {
                    case X:
                        double xm = mult * to.getOffsetX();
                        double z0 = x * xm;
                        x = z * -xm;
                        z = z0;
                        break;
                    case Y:
                        double ym = mult * to.getOffsetY();
                        double z1 = y * ym;
                        y = z * -ym;
                        z = z1;
                        break;
                    case Z: {
                        if (axis != Axis.Y) {
                            y = -y;
                            z = -z;
                        } else {
                            x = -x;
                            z = -z;
                        }
                        break;
                    }
                }
                break;
            }
        }

        return new Vec3d(x + ox, y + oy, z + oz);
    }

    public static Axis maxAbs(Vec3d vec) {
        Axis result = null;
        double x = 0;
        for(Axis axis : Axis.values()) {
            double d = vec.getComponentAlongAxis(axis);
            d = Math.abs(d);
            if(d > x) {
                result = axis;
                x = d;
            }
        }
        return result;
    }

    public static Direction getMaxDirection(Vec3d v, double atLeast, Direction fallback) {
        Axis maxAbs = maxAbs(v);
        double d = v.getComponentAlongAxis(maxAbs);
        if(Math.abs(d) < atLeast) {
            return fallback;
        } else {
            Direction.AxisDirection dir = Direction.AxisDirection.POSITIVE;
            if(d < 0) {
                dir = Direction.AxisDirection.NEGATIVE;
            }
            return Direction.get(dir, maxAbs);
        }
    }

    public static Vec3d floorAll(Vec3d v) {
        return new Vec3d(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
    }

    public static Vec3d setAxis(Vec3d v, Axis axis, double value) {
        switch (axis) {
            case X:
                return new Vec3d(value, v.y, v.z);
            case Y:
                return new Vec3d(v.x, value, v.z);
            case Z:
                return new Vec3d(v.x, v.y, value);
        }
        throw new IllegalArgumentException("Unknown axis: " + axis.toString());
    }

    public static Vec3d fromInt(Vec3i v) {
        return new Vec3d(v.getX(), v.getY(), v.getZ());
    }

    public static Axis nextAxis(Axis axis) {
        return Axis.values()[(axis.ordinal() + 1) % Axis.values().length];
    }

    public static Direction rotateDirection(Direction dir, Direction from, Direction to) {
        Vec3d v = fromInt(dir.getVector());
        v = v.multiply(0.5);
        v = v.add(0.5, 0.5, 0.5);
        v = rotate(v, from, to);
        v = v.subtract(0.5, 0.5, 0.5);
        return getMaxDirection(v, -10f, Direction.DOWN);
    }
}
