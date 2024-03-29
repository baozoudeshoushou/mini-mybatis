package com.lin.mybatis.executor;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;
import com.lin.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 16:14:41
 */
public abstract class BaseExecutor implements Executor {

    protected Transaction transaction;

    protected Configuration configuration;

    protected Executor wrapper;

    private boolean closed;

    protected BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new MybatisException("Executor was closed.");
        }
//        clearLocalCache();
        return doUpdate(ms, parameter);
    }

    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        return query(ms, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (closed) {
            throw new MybatisException("Executor was closed.");
        }
        return doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    }

    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                           ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new MybatisException("Executor was closed.");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        }
        catch (SQLException e) {
            // Ignore. There's nothing that can be done at this point.
        }
        finally {
            transaction = null;
            closed = true;
        }
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

}
