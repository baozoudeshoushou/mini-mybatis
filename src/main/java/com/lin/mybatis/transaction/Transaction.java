package com.lin.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a database connection. Handles the connection lifecycle that comprises: its creation, preparation,
 * commit/rollback and close.
 *
 * @author linjiayi5
 */
public interface Transaction {

    /**
     * Retrieve inner database connection.
     * @return DataBase connection
     * @throws SQLException the SQL exception
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     * @throws SQLException the SQL exception
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     * @throws SQLException the SQL exception
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     * @throws SQLException the SQL exception
     */
    void close() throws SQLException;

    /**
     * Get transaction timeout if set.
     * @return the timeout
     * @throws SQLException the SQL exception
     */
    Integer getTimeout() throws SQLException;

}
