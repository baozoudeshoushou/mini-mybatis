package com.lin.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 11:33:12
 */
public class IntegerTypeHandler extends BaseTypeHandler<Integer> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter);
    }

}
