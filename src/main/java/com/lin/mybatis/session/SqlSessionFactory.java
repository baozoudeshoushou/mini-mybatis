package com.lin.mybatis.session;

/**
 * Creates an {@link SqlSession} out of a connection or a DataSource
 *
 * @Author linjiayi5
 * @Date 2023/4/26 16:25:30
 */
public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(TransactionIsolationLevel level);

    Configuration getConfiguration();

}
