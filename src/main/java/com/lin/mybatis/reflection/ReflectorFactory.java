package com.lin.mybatis.reflection;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:40:29
 */
public interface ReflectorFactory {

    Reflector findForClass(Class<?> type);

}
