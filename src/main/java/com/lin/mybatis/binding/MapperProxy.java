package com.lin.mybatis.binding;

import com.lin.mybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 10:19:18
 */
public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;

    private final Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            // Object 继承的方法不作处理
            return method.invoke(this, args);
        }

        MapperMethod mapperMethod = cachedInvoker(method);
        return mapperMethod.execute(sqlSession, args);
    }

    /**
     * Get MapperMethod from cache, or create it if not present.
     */
    private MapperMethod cachedInvoker(Method method) {
        return methodCache.computeIfAbsent(method, (m) -> {
            return new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
        });
    }

}
