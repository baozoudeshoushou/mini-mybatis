package com.lin.mybatis.executor.resultset;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.executor.parameter.ParameterHandler;
import com.lin.mybatis.executor.result.DefaultResultContext;
import com.lin.mybatis.executor.result.DefaultResultHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.ResultMap;
import com.lin.mybatis.mapping.ResultMapping;
import com.lin.mybatis.reflection.MetaClass;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.ReflectorFactory;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;
import com.lin.mybatis.type.TypeHandler;
import com.lin.mybatis.type.TypeHandlerRegistry;

import java.sql.*;
import java.util.*;

/**
 * @author linjiayi5
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private final Executor executor;

    private final Configuration configuration;

    private final MappedStatement mappedStatement;

    private final RowBounds rowBounds;

    private final ParameterHandler parameterHandler;

    private final ResultHandler<?> resultHandler;

    private final BoundSql boundSql;

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final ObjectFactory objectFactory;

    private final ReflectorFactory reflectorFactory;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler,
                                   ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;
        this.parameterHandler = parameterHandler;
        this.boundSql = boundSql;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.objectFactory = configuration.getObjectFactory();
        this.reflectorFactory = configuration.getReflectorFactory();
        this.resultHandler = resultHandler;
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        final List<Object> multipleResults = new ArrayList<>();

        int resultSetCount = 0;
        ResultSetWrapper rsw = getFirstResultSet(stmt);

        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        int resultMapCount = resultMaps.size();
        validateResultMapsCount(rsw, resultMapCount);

        while (rsw != null && resultMapCount > resultSetCount) {
            ResultMap resultMap = resultMaps.get(resultSetCount);
            handleResultSet(rsw, resultMap, multipleResults, null);
            rsw = getNextResultSet(stmt);
            cleanUpAfterHandlingResultSet();
            resultSetCount++;
        }

        // ?
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }

    private ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            // move forward to get the first resultset in case the driver
            // doesn't return the resultset as the first result (HSQLDB 2.1)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else if (stmt.getUpdateCount() == -1) {
                // no more results. Must be no resultset
                break;
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    private ResultSetWrapper getNextResultSet(Statement stmt) throws SQLException {
        // Making this method tolerant of bad JDBC drivers
        try {
            // Crazy Standard JDBC way of determining if there are more results
            if (stmt.getConnection().getMetaData().supportsMultipleResultSets()
                    && (stmt.getMoreResults() || (stmt.getUpdateCount() != -1))) {
                ResultSet rs = stmt.getResultSet();
                if (rs == null) {
                    return getNextResultSet(stmt);
                }
                return new ResultSetWrapper(rs, configuration);
            }
        } catch (Exception ignore) {
            // Intentionally ignored.
        }
        return null;
    }

    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults,
                                 ResultMapping parentMapping) throws SQLException {
        try {
            if (parentMapping != null) {
                handleRowValues(rsw, resultMap, null, RowBounds.DEFAULT, parentMapping);
            }
            else if (resultHandler == null) {
                // 一般都不指定 resultHandler，走这里
                DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
                handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
                multipleResults.add(defaultResultHandler.getResultList());
            }
            else {
                handleRowValues(rsw, resultMap, resultHandler, rowBounds, null);
            }
        }
        finally {
            closeResultSet(rsw.getResultSet());
        }
    }

    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler,
                                RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        handleRowValuesForSimpleResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
    }

    private void handleRowValuesForSimpleResultMap(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler,
                                                   RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        DefaultResultContext resultContext = new DefaultResultContext();
        ResultSet resultSet = rsw.getResultSet();
        while (resultContext.getResultCount() < rowBounds.getLimit() && resultSet.next()) {
            Object rowValue = getRowValue(rsw, resultMap);
            storeObject(resultHandler, resultContext, rowValue, parentMapping, resultSet);
        }
    }

    private void storeObject(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue,
                             ResultMapping parentMapping, ResultSet rs) throws SQLException {
        callResultHandler(resultHandler, resultContext, rowValue);
    }

    @SuppressWarnings("unchecked" /* because ResultHandler<?> is always ResultHandler<Object> */)
    private void callResultHandler(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue) {
        resultContext.nextResultObject(rowValue);
        ((ResultHandler<Object>) resultHandler).handleResult(resultContext);
    }

    /**
     * 获取一行的值
     */
    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        // 根据返回类型，实例化对象
        Object rowValue = createResultObject(rsw, resultMap, null);
        if (rowValue != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
            // 不存在类型处理器
            final MetaObject metaObject = configuration.newMetaObject(rowValue);
            boolean foundValues = applyAutomaticMappings(rsw, resultMap, metaObject, null);
            foundValues = applyPropertyMappings(rsw, resultMap, metaObject, null) || foundValues;
        }
        return rowValue;
    }

    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        final List<Class<?>> constructorArgTypes = new ArrayList<>();
        final List<Object> constructorArgs = new ArrayList<>();
        return createResultObject(rsw, resultMap, constructorArgTypes, constructorArgs, columnPrefix);
    }

    /**
     * 创建结果
     */
    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, List<Class<?>> constructorArgTypes,
                                      List<Object> constructorArgs, String columnPrefix) throws SQLException {
        final Class<?> resultType = resultMap.getType();
        final MetaClass metaType = MetaClass.forClass(resultType, reflectorFactory);
        if (resultType.isInterface() || metaType.hasDefaultConstructor()) {
            return objectFactory.create(resultType);
        }
        throw new RuntimeException("Do not know how to create an instance of " + resultType);
    }

    private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
        boolean foundValues = false;
        for (String columnName : unmappedColumnNames) {
            String propertyName = columnName;
            if (columnPrefix != null && !columnPrefix.isEmpty()) {
                // When columnPrefix is specified,ignore columns without the prefix.
                if (columnName.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
                    propertyName = columnName.substring(columnPrefix.length());
                } else {
                    continue;
                }
            }
            // 开始设置属性值
            final String property = metaObject.findProperty(propertyName, false);
            if (property != null && metaObject.hasSetter(property)) {
                final Class<?> propertyType = metaObject.getSetterType(property);
                if (typeHandlerRegistry.hasTypeHandler(propertyType)) {
                    final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
                    final Object value = typeHandler.getResult(rsw.getResultSet(), columnName);
                    if (value != null) {
                        foundValues = true;
                    }
                    if (value != null || !propertyType.isPrimitive()) {
                        metaObject.setValue(property, value);
                    }
                }
            }
        }
        return foundValues;
    }

    private boolean applyPropertyMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final Set<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
        boolean foundValues = false;
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        for (ResultMapping resultMapping : resultMappings) {
            String column = resultMapping.getColumn();
            if (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) {
                final TypeHandler<?> typeHandler = resultMapping.getTypeHandler();
                Object value = typeHandler.getResult(rsw.getResultSet(), column);
                final String property = resultMapping.getProperty();
                if (property != null && value != null) {
                    metaObject.setValue(property, value);
                    foundValues = true;
                }
            }
        }
        return foundValues;
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    private void cleanUpAfterHandlingResultSet() {

    }

    private void validateResultMapsCount(ResultSetWrapper rsw, int resultMapCount) {
        if (rsw != null && resultMapCount < 1) {
            throw new MybatisException(
                    "A query was run and no Result Maps were found for the Mapped Statement '" + mappedStatement.getId()
                            + "'. 'resultType' or 'resultMap' must be specified when there is no corresponding method.");
        }
    }

}
