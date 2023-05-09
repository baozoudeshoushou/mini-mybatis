package com.lin.mybatis.mapping;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.util.Assert;

import java.util.Map;

/**
 * @author linjiayi5
 */
public class MappedStatement {

    private String resource;

    private Configuration configuration;

    private String id;

    private SqlCommandType sqlCommandType;

    private BoundSql boundSql;

    public MappedStatement(Configuration configuration, String id, SqlCommandType sqlCommandType, BoundSql boundSql) {
        Assert.notNull(configuration, "Configuration can not be null");
        Assert.notNull(id, "Id can not be null");
        Assert.notNull(sqlCommandType, "SqlCommandType can not be null");
        Assert.notNull(boundSql, "BoundSql can not be null");

        this.configuration = configuration;
        this.id = id;
        this.sqlCommandType = sqlCommandType;
        this.boundSql = boundSql;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }

}
