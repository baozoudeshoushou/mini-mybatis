package com.lin.mybatis.builder;

import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.ParameterMapping;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.session.Configuration;

import java.util.List;

/**
 * 静态 SQL 源，维护了 SQL（已被解析，expression 已经被 ? 替换）、相应的 ParameterMapping List
 * @author linjiayi5
 */
public class StaticSqlSource implements SqlSource {

    private final String sql;
    private final List<ParameterMapping> parameterMappings;
    private final Configuration configuration;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

}
