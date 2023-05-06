package com.lin.mybatis.transaction.jdbc;

import com.lin.mybatis.session.TransactionIsolationLevel;
import com.lin.mybatis.transaction.Transaction;
import com.lin.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author linjiayi5
 */
public class JdbcTransactionFactory implements TransactionFactory {


    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }

}
