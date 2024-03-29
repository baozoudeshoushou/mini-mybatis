package com.lin.mybatis.mapping;

import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.session.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绑定的 SQL, 是从 SqlSource 而来，将动态内容都处理完成得到的 SQL 语句字符串，其中包括 ?, 还有绑定的参数
 *
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

    private List<ParameterMapping> parameterMappings;

    private final Object parameterObject;

    private final Map<String, Object> additionalParameters;

    private final MetaObject metaParameters;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterObject = parameterObject;
        this.additionalParameters = new HashMap<>();
        this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

}
