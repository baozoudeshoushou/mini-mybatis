package com.lin.mybatis.session.defaults;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;
import com.lin.mybatis.session.SqlSessionFactory;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:26:50
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

}
