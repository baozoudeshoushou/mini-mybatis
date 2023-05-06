package com.lin.mybatis.binding;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 11:10:57
 */
public class MapperMethod {

    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.command = new SqlCommand(config, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (command.getType()) {
            case INSERT: {
                result = null;
                break;
            }
            case UPDATE: {
                result = null;
                break;
            }
            case DELETE: {
                result = null;
                break;
            }
            case SELECT: {
                result = executeForMany(sqlSession, args);
                break;
            }
            default: throw new MybatisException("Unknown execution method for: " + command.getName());
        }

        return result;
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        return sqlSession.selectList(command.getName(), args);
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

}
