package com.lin.mybatis.reflection.invoker;

import com.lin.mybatis.exceptions.MybatisException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author linjiayi5
 * @Date 2023/5/12 17:08:30
 */
public class AmbiguousMethodInvoker extends MethodInvoker {

    private final String exceptionMessage;

    public AmbiguousMethodInvoker(Method method, String exceptionMessage) {
        super(method);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        throw new MybatisException(exceptionMessage);
    }

}
