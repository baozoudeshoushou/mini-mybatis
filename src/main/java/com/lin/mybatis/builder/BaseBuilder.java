package com.lin.mybatis.builder;

import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.type.TypeAliasRegistry;
import com.lin.mybatis.type.TypeHandler;
import com.lin.mybatis.type.TypeHandlerRegistry;

/**
 * @Author linjiayi5
 * @Date 2023/5/23 16:55:16
 */
public class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;

    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected <T> Class<? extends T> resolveClass(String alias) {
        if (alias == null) {
            return null;
        }
        return resolveAlias(alias);
    }

    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }

        return typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
    }

}
