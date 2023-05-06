package com.lin.mybatis.session;

import com.lin.mybatis.binding.MapperRegistry;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.mapping.MappedStatement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:26:16
 */
public class Configuration {

    protected Environment environment;

    protected final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();

    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

}
