package com.lin.mybatis.executor;

import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

/**
 * @author linjiayi5
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

}
