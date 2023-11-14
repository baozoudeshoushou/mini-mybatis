package com.lin.mybatis.session;

import com.lin.mybatis.binding.MapperRegistry;
import com.lin.mybatis.datasource.HikariDatasourceFactory;
import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.executor.SimpleExecutor;
import com.lin.mybatis.reflection.DefaultReflectorFactory;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.ReflectorFactory;
import com.lin.mybatis.reflection.factory.DefaultObjectFactory;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.lin.mybatis.reflection.wrapper.ObjectWrapperFactory;
import com.lin.mybatis.executor.parameter.ParameterHandler;
import com.lin.mybatis.executor.resultset.DefaultResultSetHandler;
import com.lin.mybatis.executor.resultset.ResultSetHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.scripting.LanguageDriverRegistry;
import com.lin.mybatis.scripting.xmltags.XMLLanguageDriver;
import com.lin.mybatis.statement.PreparedStatementHandler;
import com.lin.mybatis.statement.StatementHandler;
import com.lin.mybatis.transaction.Transaction;
import com.lin.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.lin.mybatis.type.TypeAliasRegistry;
import com.lin.mybatis.type.TypeHandlerRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linjiayi5
 */
public class Configuration {

    protected Environment environment;

    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;

    protected final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();

    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    protected String databaseId;

    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry(this);

    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

    protected final Set<String> loadedResources = new HashSet<>();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("HIKARI", HikariDatasourceFactory.class);

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return this.typeAliasRegistry;
    }

    public Executor newExecutor(Transaction tx, ExecutorType executorType) {
        // TODO 不紧要
        return new SimpleExecutor(this, tx);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, ms, parameter, resultHandler, boundSql);
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return this.typeHandlerRegistry;
    }

    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }

    /**
     * Gets the language driver.
     *
     * @param langClass the lang class
     * @return the language driver
     */
    public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
        if (langClass == null) {
            return languageRegistry.getDefaultDriver();
        }
        languageRegistry.register(langClass);
        return languageRegistry.getDriver(langClass);
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    /**
     * 创建参数处理器
     */
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        // TODO 插件的一些参数，也是在这里处理，暂不实现
//        return (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    public Object getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

}
