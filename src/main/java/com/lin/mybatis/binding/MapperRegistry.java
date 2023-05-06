package com.lin.mybatis.binding;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linjiayi5
 */
public class MapperRegistry {

    private final Configuration config;

    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);

        if (mapperProxyFactory == null) {
            throw new MybatisException("Type " + type + " is not known to the MapperRegistry.");
        }

        return mapperProxyFactory.newInstance(sqlSession);
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type) {
        if (!type.isInterface()) {
            return;
        }

        if (hasMapper(type)) {
            throw new MybatisException("Type " + type + " is already known to the MapperRegistry.");
        }

        knownMappers.put(type, new MapperProxyFactory<>(type));

        // TODO parse the mapper annotation

    }

}
