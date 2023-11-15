package com.lin.mybatis.scripting.defaults;

import com.lin.mybatis.executor.parameter.ParameterHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.ParameterMapping;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.type.JdbcType;
import com.lin.mybatis.type.TypeHandler;
import com.lin.mybatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/9 16:09:14
 */
public class DefaultParameterHandler implements ParameterHandler {

    protected final TypeHandlerRegistry typeHandlerRegistry;

    private final MappedStatement mappedStatement;

    private final Object parameterObject;

    private final BoundSql boundSql;

    private final Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return this.parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String propertyName = parameterMapping.getProperty();
                Object value;
                if (parameterObject == null) {
                    value = null;
                }
                else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    // Integer 等基础类型，直接走这里
                    value = parameterObject;
                }
                else {
                    // Bean 类，用反射获取值
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                JdbcType jdbcType = parameterMapping.getJdbcType();

                // 设置参数
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                typeHandler.setParameter(ps, i + 1, value, jdbcType);
            }
        }
    }

}
