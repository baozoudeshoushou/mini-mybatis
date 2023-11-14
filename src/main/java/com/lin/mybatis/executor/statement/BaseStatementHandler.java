package com.lin.mybatis.executor.statement;

import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.executor.parameter.ParameterHandler;
import com.lin.mybatis.executor.resultset.ResultSetHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author linjiayi5
 * @date 2023/5/6
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;

    protected final ResultSetHandler resultSetHandler;

    protected final ParameterHandler parameterHandler;

    protected final Executor executor;

    protected final MappedStatement mappedStatement;

    protected BoundSql boundSql;

    protected final Object parameterObject;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
                                RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;

        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }
        this.boundSql = boundSql;

        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql);

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
