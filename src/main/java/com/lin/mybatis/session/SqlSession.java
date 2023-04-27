package com.lin.mybatis.session;

import java.io.Closeable;
import java.util.List;

/**
 * 核心接口。Mybatis 通过动态代理将 Mapper 的方法转为调用 SqlSession 提供的增删改查方法
 * The primary Java interface for working with MyBatis. Through this interface you can execute commands, get mappers and
 * manage transactions.
 *
 * @Author linjiayi5
 * @Date 2023/4/26 16:23:56
 */
public interface SqlSession extends Closeable {

    /**
     * Retrieve a single row mapped from the statement key.
     *
     * @param <T> the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @return Mapped object
     */
    <T> T selectOne(String statement);

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     *
     * @param <T> the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * Retrieve a list of mapped objects from the statement key.
     *
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     *
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter);

    /**
     * Retrieves current configuration.
     *
     * @return Configuration
     */
    Configuration getConfiguration();

}
