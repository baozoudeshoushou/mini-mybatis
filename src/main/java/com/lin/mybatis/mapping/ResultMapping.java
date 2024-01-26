package com.lin.mybatis.mapping;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.type.TypeHandler;
import com.lin.mybatis.type.TypeHandlerRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linjiayi5
 */
public class ResultMapping {

    private Configuration configuration;
    private String property;
    private String column;
    private Class<?> javaType;
    private TypeHandler<?> typeHandler;
    private List<ResultFlag> flags;

    ResultMapping() {

    }

    public static class Builder {
        private final ResultMapping resultMapping = new ResultMapping();

        public Builder(Configuration configuration, String property) {
            resultMapping.configuration = configuration;
            resultMapping.property = property;
            resultMapping.flags = new ArrayList<>();
        }

        public Builder(Configuration configuration, String property, String column, Class<?> javaType) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.javaType = javaType;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            resultMapping.typeHandler = typeHandler;
            return this;
        }

        public Builder flags(List<ResultFlag> flags) {
            resultMapping.flags = flags;
            return this;
        }

        public ResultMapping build() {
            resolveTypeHandler();
            return resultMapping;
        }

        private void resolveTypeHandler() {
            if (resultMapping.typeHandler == null && resultMapping.javaType != null) {
                Configuration configuration = resultMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                resultMapping.typeHandler = typeHandlerRegistry.getTypeHandler(resultMapping.javaType, null);
            }
        }

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public List<ResultFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<ResultFlag> flags) {
        this.flags = flags;
    }

}
