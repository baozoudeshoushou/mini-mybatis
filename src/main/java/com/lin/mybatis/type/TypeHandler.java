package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 11:21:10
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}
