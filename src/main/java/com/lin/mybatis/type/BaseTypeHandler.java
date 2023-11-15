package com.lin.mybatis.type;

import com.lin.mybatis.exceptions.MybatisException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 11:28:03
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            if (jdbcType == null) {
                throw new MybatisException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
            }
            try {
                ps.setNull(i, jdbcType.TYPE_CODE);
            }
            catch (SQLException e) {
                throw new MybatisException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
                        + "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
                        + "Cause: " + e, e);
            }
        }
        else {
            try {
                setNonNullParameter(ps, i, parameter, jdbcType);
            }
            catch (Exception e) {
                throw new MybatisException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . "
                        + "Try setting a different JdbcType for this parameter or a different configuration property. " + "Cause: "
                        + e, e);
            }
        }
    }

    public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;


    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return getNullableResult(rs, columnName);
        } catch (Exception e) {
            throw new MybatisException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
        }
    }

    /**
     * Gets the nullable result.
     *
     * @param rs the rs
     * @param columnName Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
     * @return the nullable result
     * @throws SQLException the SQL exception
     */
    public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

}
