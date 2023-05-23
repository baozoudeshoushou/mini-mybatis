package com.lin.mybatis.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:40:16
 */
public class DefaultReflectorFactory implements ReflectorFactory {

    private boolean classCacheEnabled = true;

    private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    @Override
    public Reflector findForClass(Class<?> type) {
        if (classCacheEnabled) {
            return reflectorMap.computeIfAbsent(type, Reflector::new);
        }
        return new Reflector(type);
    }

}
