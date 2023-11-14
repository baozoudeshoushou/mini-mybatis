package com.lin.mybatis.executor;

import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.executor.statement.StatementHandler;
import com.lin.mybatis.session.RowBounds;
import com.lin.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author linjiayi5
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                  ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
        Connection connection = transaction.getConnection();
        Statement stmt = handler.prepare(connection, transaction.getTimeout());
        handler.parameterize(stmt);
        return handler.query(stmt, resultHandler);
    }

}
