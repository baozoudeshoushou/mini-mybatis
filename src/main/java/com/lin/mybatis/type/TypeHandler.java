package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 11:21:10
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    /**
     * Gets the result.
     *
     * @param rs the rs
     * @param columnName Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
     * @return the result
     * @throws SQLException the SQL exception
     */
    T getResult(ResultSet rs, String columnName) throws SQLException;

}
