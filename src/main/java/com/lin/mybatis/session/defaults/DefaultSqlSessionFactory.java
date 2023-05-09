package com.lin.mybatis.session.defaults;

import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;
import com.lin.mybatis.session.SqlSessionFactory;
import com.lin.mybatis.transaction.Transaction;
import com.lin.mybatis.transaction.TransactionFactory;

/**
 * @author linjiayi5
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Environment environment = configuration.getEnvironment();
        TransactionFactory transactionFactory = environment.getTransactionFactory();
        boolean autoCommit = false;
        Transaction tx = transactionFactory.newTransaction(environment.getDataSource(), null, autoCommit);
        Executor executor = configuration.newExecutor(tx);
        return new DefaultSqlSession(configuration, executor, autoCommit);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
