package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @Author linjiayi5
 * @Date 2024/1/26 18:00:27
 */
public class SqlTimestampTypeHandler extends BaseTypeHandler<Timestamp> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, parameter);
    }

    @Override
    public Timestamp getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName);
    }

}
