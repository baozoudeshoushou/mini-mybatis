package com.lin.mybatis.builder;

import com.lin.mybatis.mapping.ParameterMapping;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.parsing.GenericTokenParser;
import com.lin.mybatis.parsing.TokenHandler;
import com.lin.mybatis.reflection.MetaClass;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL 源码构建器
 * @author linjiayi5
 */
public class SqlSourceBuilder extends BaseBuilder {

    private static Logger logger = LoggerFactory.getLogger(SqlSourceBuilder.class);

    private static final String PARAMETER_PROPERTIES = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        // 返回静态 SQL
        return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
    }

    /**
     * 用于处理 SQL 中的 expression，例如 #{id} 里的 id
     */
    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private final List<ParameterMapping> parameterMappings = new ArrayList<>();
        private final Class<?> parameterType;
        private final MetaObject metaParameters;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
            this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        private ParameterMapping buildParameterMapping(String content) {
            Map<String, String> propertiesMap = new ParameterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType;
            if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
                // 一般基础类型走这里，java.lang.Long
                propertyType = parameterType;
            } else if (property != null) {
                // Bean 类，获取 getter 的返回类型
                MetaClass metaClass = MetaClass.forClass(parameterType, configuration.getReflectorFactory());
                if (metaClass.hasGetter(property)) {
                    propertyType = metaClass.getGetterType(property);
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }

            logger.info("构建参数映射 property：{} propertyType：{}", property, propertyType);
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            return builder.build();
        }

    }

}
