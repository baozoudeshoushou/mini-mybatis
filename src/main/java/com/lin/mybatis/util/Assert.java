package com.lin.mybatis.util;

/**
 * @Author linjiayi5
 * @Date 2023/4/27 15:28:30
 */
public class Assert {

    public static void isTrue(boolean expression, String errorMsg) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static void isFalse(boolean expression, String errorMsg) throws IllegalArgumentException {
        if (expression) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static void isNull(Object object, String errorMsg) throws IllegalArgumentException {
        if (object != null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static <T> T notNull(T object, String errorMsg) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(errorMsg);
        }
        return object;
    }

}
