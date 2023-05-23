package com.lin.mybatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * @author linjiayi5
 */
public interface Invoker {

    Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

    Class<?> getType();

}
