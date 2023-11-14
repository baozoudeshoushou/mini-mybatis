package com.lin.mybatis.mapping;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.type.JdbcType;
import com.lin.mybatis.type.TypeHandler;
import com.lin.mybatis.type.TypeHandlerRegistry;

/**
 * 维护了参数名、参数类型，类型处理器。例如 property = id, javaType = java.lang.Long, typeHandler = LongTypeHandler
 * @author linjiayi5
 */
public class ParameterMapping {

    private Configuration configuration;

    private String property;

    /** javaType = int */
    private Class<?> javaType = Object.class;

    private JdbcType jdbcType;

    private TypeHandler<?> typeHandler;

    private ParameterMapping() {

    }

    public static class Builder {

        private final ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            parameterMapping.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapping build() {
            resolveTypeHandler();
            validate();
            return parameterMapping;
        }

        private void validate() {
            // TODO 验证 不紧要
        }

        private void resolveTypeHandler() {
            if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
            }
        }

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

}
