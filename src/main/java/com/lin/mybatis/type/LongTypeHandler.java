package com.lin.mybatis.type;

import java.sql.PreparedStatement;
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

}
