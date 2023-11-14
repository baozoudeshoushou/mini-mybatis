package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

}