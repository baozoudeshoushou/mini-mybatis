package com.lin.mybatis.type;

import com.lin.mybatis.session.Configuration;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linjiayi5
 * @date 2023/5/10 11:17:12
 */
public class TypeHandlerRegistry {

    /**  <JdbcType, Handler> */
    private final Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);

    /**  <JavaType, <JdbcType, Handler>>  */
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

    private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();

    public TypeHandlerRegistry(Configuration configuration) {
        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(JdbcType.INTEGER, new IntegerTypeHandler());

        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());

        register(String.class, new StringTypeHandler());
        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
        register(String.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
        register(JdbcType.CHAR, new StringTypeHandler());
        register(JdbcType.VARCHAR, new StringTypeHandler());
        register(JdbcType.LONGVARCHAR, new StringTypeHandler());

        register(Object.class, new ObjectTypeHandler());
        register(Object.class, JdbcType.OTHER, new ObjectTypeHandler());
        register(JdbcType.OTHER, new ObjectTypeHandler());

        register(Date.class, new DateTypeHandler());
        register(JdbcType.TIMESTAMP, new DateTypeHandler());

        register(java.sql.Timestamp.class, new SqlTimestampTypeHandler());
    }

    public void register(JdbcType jdbcType, TypeHandler<?> handler) {
        jdbcTypeHandlerMap.put(jdbcType, handler);
    }

    public <T> void register(Class<T> javaType, TypeHandler<? extends T> typeHandler) {
        register((Type) javaType, typeHandler);
    }

    private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
        register(javaType, null, typeHandler);
    }

    private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
        if (javaType != null) {
            Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
            if (map == null || map == NULL_TYPE_HANDLER_MAP) {
                map = new HashMap<>();
            }
            map.put(jdbcType, handler);
            typeHandlerMap.put(javaType, map);
        }
        allTypeHandlersMap.put(handler.getClass(), handler);
    }

    public boolean hasTypeHandler(Class<?> javaType) {
        return hasTypeHandler(javaType, null);
    }

    public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        return javaType != null && getTypeHandler((Type) javaType, jdbcType) != null;
    }

    public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
        return getTypeHandler((Type) type, jdbcType);
    }

    private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = typeHandlerMap.get(type);
        TypeHandler<?> handler = null;
        if (jdbcHandlerMap != null) {
            handler = jdbcHandlerMap.get(jdbcType);
            if (handler == null) {
                handler = jdbcHandlerMap.get(null);
            }
        }
        return (TypeHandler<T>) handler;
    }

    public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> handlerType) {
        return allTypeHandlersMap.get(handlerType);
    }

}
