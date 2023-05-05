package com.lin.mybatis.session;

import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.parsing.XNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:26:16
 */
public class Configuration {

    protected Environment environment;

    protected final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

}
