package com.lin.mybatis.binding;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.reflection.ParamNameResolver;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 11:10:57
 */
public class MapperMethod {

    private final SqlCommand command;

    private final MethodSignature method;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.command = new SqlCommand(config, mapperInterface, method);
        this.method = new MethodSignature(config, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (command.getType()) {
            case INSERT: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.insert(command.getName(), param);
                break;
            }
            case UPDATE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.update(command.getName(), param);
                break;
            }
            case DELETE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.delete(command.getName(), param);
                break;
            }
            case SELECT: {
                Object param = method.convertArgsToSqlCommandParam(args);
                if (method.returnsMany) {
                    result = sqlSession.selectList(command.getName(), param);
                }
                else {
                    result = sqlSession.selectOne(command.getName(), param);
                }
                break;
            }
            default: throw new MybatisException("Unknown execution method for: " + command.getName());
        }

        return result;
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        // TODO
        return null;
    }


    /** 继承 HashMap，实现更严格的 get 方法*/
    public static class ParamMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -2212268410512043556L;

        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new MybatisException("Parameter '" + key + "' not found. Available parameters are " + keySet());
            }
            return super.get(key);
        }

    }

    public static class SqlCommand {

        /** Mapper id */
        private final String name;

        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            final String methodName = method.getName();
            final Class<?> declaringClass = method.getDeclaringClass();
            MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);

            if (ms == null) {
                throw new MybatisException("Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
            }

            name = ms.getId();
            type = ms.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }

        /**
         * 递归查找当前 interface 和其父 interface，获取 MappedStatement
         */
        private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass, Configuration configuration) {
            //  先从当前接口找
            String statementId = mapperInterface.getName() + "." + methodName;
            if (configuration.hasStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            }

            // 当前接口就是定义这个方法的接口，说明的确不存在了，递归终止
            if (mapperInterface.equals(declaringClass)) {
                return null;
            }

            // 否则还需要从父接口查找
            for (Class<?> superInterface : mapperInterface.getInterfaces()) {
                if (declaringClass.isAssignableFrom(superInterface)) {
                    MappedStatement ms = resolveMappedStatement(superInterface, methodName, declaringClass, configuration);
                    if (ms != null) {
                        return ms;
                    }
                }
            }

            return null;
        }

    }

    public static class MethodSignature {

        private final boolean returnsMany;
        private final Class<?> returnType;
        private final ParamNameResolver paramNameResolver;

        public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
            this.returnType = method.getReturnType();
            this.returnsMany = configuration.getObjectFactory().isCollection(returnType) || returnType.isArray();
            this.paramNameResolver = new ParamNameResolver(configuration, method);
        }

        public Object convertArgsToSqlCommandParam(Object[] args) {
            return paramNameResolver.getNamedParams(args);
        }

    }

}
