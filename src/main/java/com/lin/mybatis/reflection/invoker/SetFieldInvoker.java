package com.lin.mybatis.reflection.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author linjiayi5
 * @Date 2023/5/16 11:29:03
 */
public class SetFieldInvoker implements Invoker {

    private final Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            field.set(target, args[0]);
        }
        catch (IllegalAccessException e) {
            field.setAccessible(true);
            field.set(target, args[0]);
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
