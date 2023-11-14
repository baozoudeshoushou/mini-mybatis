package com.lin.mybatis.executor.statement;

import com.lin.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 封装对 JDBC Statement 的操作
 * @author linjiayi5
 */
public interface StatementHandler {

    /**
     * 创建 JDBC Statement 并完成属性设置
     */
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    /**
     * 为参数占位符设置值
     */
    void parameterize(Statement statement) throws SQLException;

    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

}
