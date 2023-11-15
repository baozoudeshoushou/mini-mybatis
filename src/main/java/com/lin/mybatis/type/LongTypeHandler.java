package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author linjiayi5
 * @Date 2023/11/13 20:12:02
 */
public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter);
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long result = rs.getLong(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

}
