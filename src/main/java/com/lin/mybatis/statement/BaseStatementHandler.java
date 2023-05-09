package com.lin.mybatis.statement;

import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.executor.resultset.ResultSetHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 16:55:32
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;

    protected final ResultSetHandler resultSetHandler;

    protected final Executor executor;

    protected final MappedStatement mappedStatement;

    protected BoundSql boundSql;

    protected final Object parameterObject;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
                                ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.boundSql = boundSql;

        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);

        this.parameterObject = parameterObject;
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement statement = instantiateStatement(connection);
        // 参数设置，可以被抽取，提供配置
        statement.setQueryTimeout(350);
        statement.setFetchSize(10000);

        return statement;
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

}
