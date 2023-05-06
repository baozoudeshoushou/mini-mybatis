package com.lin.mybatis.mapping;

import com.lin.mybatis.transaction.TransactionFactory;
import com.lin.mybatis.util.Assert;

import javax.sql.DataSource;

/**
 * @Author linjiayi5
 * @Date 2023/4/27 15:26:14
 */
public class Environment {

    private final String id;

    private final TransactionFactory transactionFactory;

    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        Assert.notNull(id, "Parameter 'id' must not be null");
        Assert.notNull(transactionFactory, "Parameter 'transactionFactory' must not be null");
        Assert.notNull(dataSource, "Parameter 'dataSource' must not be null");
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public String getId() {
        return this.id;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

}
