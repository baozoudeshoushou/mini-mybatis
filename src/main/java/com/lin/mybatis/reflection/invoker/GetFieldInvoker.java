package com.lin.mybatis.reflection.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author linjiayi5
 * @Date 2023/5/16 11:31:20
 */
public class GetFieldInvoker implements Invoker {

    private final Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            return field.get(target);
        }
        catch (IllegalAccessException e) {
            field.setAccessible(true);
            return field.get(target);
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
