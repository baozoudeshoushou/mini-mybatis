package com.lin.mybatis.session.defaults;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;
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
        return this.selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.selectList(statement, parameter, RowBounds.DEFAULT);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return selectList(statement, parameter, rowBounds, Executor.NO_RESULT_HANDLER);
    }

    private <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.query(ms, parameter, rowBounds, handler);
        } catch (SQLException e) {
            throw new MybatisException("Error querying database.", e);
        }
    }

    @Override
    public int insert(String statement) {
        return insert(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    @Override
    public int update(String statement, Object parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.update(ms, parameter);
        } catch (SQLException e) {
            throw new MybatisException("Error updating database.  Cause: " + e, e);
        }
    }

    @Override
    public int delete(String statement) {
        return update(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public void commit() {
        commit(false);
    }

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(isCommitOrRollbackRequired(false));
        } catch (SQLException e) {
            throw new MybatisException("Error committing transaction.  Cause: " + e, e);
        }
    }

    @Override
    public void rollback() {
        rollback(false);
    }

    @Override
    public void rollback(boolean force) {
        try {
            executor.rollback(isCommitOrRollbackRequired(force));
        } catch (SQLException e) {
            throw new MybatisException("Error rolling back transaction.  Cause: " + e, e);
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
    public void close() {
        executor.close(isCommitOrRollbackRequired(false));
    }

    private boolean isCommitOrRollbackRequired(boolean force) {
        return !autoCommit || force;
    }

}
