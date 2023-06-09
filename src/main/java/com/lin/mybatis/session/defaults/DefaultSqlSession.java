package com.lin.mybatis.session.defaults;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * @author linjiayi5
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    private final Executor executor;

    private final boolean autoCommit;

    public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit = autoCommit;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = this.selectList(statement, parameter);

        if (list.size() == 1) {
            return list.get(0);
        }

        if (list.size() > 1) {
            throw new MybatisException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        }

        return null;
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
        } catch (SQLException e) {
            throw new MybatisException("Error querying database.", e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public void close() throws IOException {

    }

}
