package com.lin.mybatis.builder;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.mapping.*;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 映射器构建助手
 * @author linjiayi5
 */
public class MapperBuilderAssistant extends BaseBuilder {

    private String currentNamespace;

    private final String resource;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        if (currentNamespace == null) {
            throw new MybatisException("The mapper element requires a namespace attribute to be specified.");
        }

        if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
            throw new MybatisException("Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
        }

        this.currentNamespace = currentNamespace;
    }

    /**
     * 给 id 前面加上 namespace
     */
    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }

        if (isReference) {
            // is it qualified with any namespace yet?
            if (base.contains(".")) {
                return base;
            }
        }
        else {
            // is it qualified with this namespace yet?
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new MybatisException("Dots are not allowed in element names, please remove it from " + base);
            }
        }

        return currentNamespace + "." + base;
    }

    public ResultMap addResultMap(String id, Class<?> type, List<ResultMapping> resultMappings) {
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, id, type, resultMappings);
        ResultMap resultMap = inlineResultMapBuilder.build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    public MappedStatement addMappedStatement(String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout,
                                              Class<?> parameterTypeClass, String resultMap, Class<?> resultTypeClass, String databaseId, LanguageDriver langDriver) {
        id = applyCurrentNamespace(id, false);
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType, resultTypeClass)
                .fetchSize(fetchSize).timeout(timeout);
        statementBuilder.resultMaps(getStatementResultMaps(resultMap, resultTypeClass, id));
        MappedStatement statement = statementBuilder.build();

        configuration.addMappedStatement(statement);
        return statement;
    }

    /**
     * 获取 ResultMaps，逻辑是有配置 resultMap 就用 resultMap，没有就用 resultType
     * 一般 resultType 即可满足，会自动创建一个 ResultMap，基于属性名称映射列到 JavaBean 的属性上*
     */
    private List<ResultMap> getStatementResultMaps(String resultMap, Class<?> resultType, String statementId) {
        resultMap = applyCurrentNamespace(resultMap, true);

        List<ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                try {
                    resultMaps.add(configuration.getResultMap(resultMapName.trim()));
                }
                catch (IllegalArgumentException e) {
                    throw new MybatisException(
                            "Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'", e);
                }
            }
        }
        else if (resultType != null) {
            ResultMap inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline", resultType, new ArrayList<>()).build();
            resultMaps.add(inlineResultMap);
        }

        return resultMaps;
    }


}
