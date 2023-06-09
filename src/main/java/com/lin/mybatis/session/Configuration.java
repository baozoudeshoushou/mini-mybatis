package com.lin.mybatis.session;

import com.lin.mybatis.binding.MapperRegistry;
import com.lin.mybatis.datasource.HikariDatasourceFactory;
import com.lin.mybatis.executor.Executor;
import com.lin.mybatis.executor.SimpleExecutor;
import com.lin.mybatis.executor.resultset.DefaultResultSetHandler;
import com.lin.mybatis.executor.resultset.ResultSetHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.statement.PreparedStatementHandler;
import com.lin.mybatis.statement.StatementHandler;
import com.lin.mybatis.transaction.Transaction;
import com.lin.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.lin.mybatis.type.TypeAliasRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:26:16
 */
public class Configuration {

    protected Environment environment;

    protected final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();

    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("HIKARI", HikariDatasourceFactory.class);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
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

    public Executor newExecutor(Transaction tx) {
        return new SimpleExecutor(this, tx);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, ms, parameter, resultHandler, boundSql);
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

}
