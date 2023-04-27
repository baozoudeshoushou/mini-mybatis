package com.lin.mybatis.session.defaults;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:27:40
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <T> T selectOne(String statement) {
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

}
