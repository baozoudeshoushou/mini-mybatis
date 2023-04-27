package com.lin.mybatis.session;

import com.lin.mybatis.builder.xml.XMLConfigBuilder;
import com.lin.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * Builds {@link SqlSession} instances.
 *
 * @Author linjiayi5
 * @Date 2023/4/26 16:23:31
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();
        return new DefaultSqlSessionFactory(configuration);
    }

}
