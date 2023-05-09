package com.lin.mybatis.mapping;

import java.util.Map;

/**
 * An actual SQL String got from an {@link SqlSource} after having processed any dynamic content. The SQL may have SQL
 * placeholders "?" and a list (ordered) of a parameter mappings with the additional information for each parameter (at
 * least the property name of the input object to read the value from).
 * <p>
 * Can also have additional parameters that are created by the dynamic language (for loops, bind...).
 *
 * @author linjiayi5
 */
public class BoundSql {

    private final String sql;

    private Map<Integer, String> parameterMappings;

    private String parameterType;

    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }

}
