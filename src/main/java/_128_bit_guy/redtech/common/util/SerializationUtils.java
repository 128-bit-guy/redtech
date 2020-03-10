package _128_bit_guy.redtech.common.util;

import java.util.Map;

public class SerializationUtils {
    public static <T extends Enum<T>>byte serializeEnumToBoolean(Map<T, Boolean> m, Class<T> c) {
        byte result = 0;
        for(int i = 0; i < c.getEnumConstants().length; ++i) {
            T v = c.getEnumConstants()[i];
            if(m.get(v)) {
                result |= ((byte) 1) << i;
            }
        }
        return result;
    }

    public static <T extends Enum<T>>void deserializeEnumToBoolean(Map<T, Boolean> m, byte b, Class<T> c) {
        for(int i = 0; i < c.getEnumConstants().length; ++i) {
            T v = c.getEnumConstants()[i];
            m.put(v, (b & ((byte)1 << i)) != (byte)0);
        }
    }

    public static <T extends Enum<T>>int enumToInt(T v) {
        if(v == null) {
            return 0;
        }
        return v.ordinal() + 1;
    }

    public static <T extends Enum<T>> T intToEnum(int x, Class<T> clazz) {
        if(x == 0) {
            return null;
        }
        return clazz.getEnumConstants()[x - 1];
    }
}
