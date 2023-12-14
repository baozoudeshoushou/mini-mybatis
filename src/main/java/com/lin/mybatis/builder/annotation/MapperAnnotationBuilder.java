package com.lin.mybatis.builder.annotation;

import com.lin.mybatis.annotations.Delete;
import com.lin.mybatis.annotations.Insert;
import com.lin.mybatis.annotations.Select;
import com.lin.mybatis.annotations.Update;
import com.lin.mybatis.binding.MapperMethod;
import com.lin.mybatis.builder.MapperBuilderAssistant;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.reflection.TypeParameterResolver;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.session.ResultHandler;
import com.lin.mybatis.session.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author linjiayi5
 */
public class MapperAnnotationBuilder {

    private static final Set<Class<? extends Annotation>> statementAnnotationTypes = Stream
            .of(Select.class, Update.class, Insert.class, Delete.class)
            .collect(Collectors.toSet());

    private final Configuration configuration;

    private final Class<?> type;

    private final MapperBuilderAssistant assistant;

    public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.configuration = configuration;
        this.type = type;
        this.assistant = new MapperBuilderAssistant(configuration, resource);
    }

    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            configuration.addLoadedResource(resource);
            assistant.setCurrentNamespace(type.getName());
            for (Method method : type.getMethods()) {
                if (!canHaveStatement(method)) {
                    continue;
                }
                parseStatement(method);
            }
        }

    }

    private static boolean canHaveStatement(Method method) {
        return !method.isBridge() && !method.isDefault();
    }

    void parseStatement(Method method) {
        final Class<?> parameterTypeClass = getParameterType(method);
        final LanguageDriver languageDriver = getLanguageDriver(method);

        AnnotationWrapper statementAnnotation = getAnnotationWrapper(method, statementAnnotationTypes);
        if (statementAnnotation != null) {
            final SqlSource sqlSource = buildSqlSource(statementAnnotation.getAnnotation(), parameterTypeClass, languageDriver, method);
            SqlCommandType sqlCommandType = statementAnnotation.getSqlCommandType();
            final String mappedStatementId = type.getName() + "." + method.getName();

            Integer fetchSize = null;
            Integer timeout = null;

            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

            String resultMapId = null;
            if (isSelect) {
                resultMapId = generateResultMapName(method);
                Class<?> returnType = getReturnType(method, type);
                assistant.addResultMap(resultMapId, returnType, new ArrayList<>());
            }

            assistant.addMappedStatement(mappedStatementId, sqlSource, sqlCommandType, fetchSize, timeout,
                    parameterTypeClass, resultMapId, getReturnType(method, type), statementAnnotation.getDatabaseId(), languageDriver);
        }

    }

    private String generateResultMapName(Method method) {
        StringBuilder suffix = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            suffix.append("-");
            suffix.append(c.getSimpleName());
        }
        if (suffix.length() < 1) {
            suffix.append("-void");
        }
        return type.getName() + "." + method.getName() + suffix;
    }

    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null) {
                    parameterType = currentParameterType;
                }
                else {
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    private LanguageDriver getLanguageDriver(Method method) {
        return configuration.getLanguageDriver(null);
    }

    private SqlSource buildSqlSource(Annotation annotation, Class<?> parameterType, LanguageDriver languageDriver, Method method) {
        if (annotation instanceof Select select) {
            return buildSqlSourceFromStrings(select.value(), parameterType, languageDriver);
        }
        if (annotation instanceof Update update) {
            return buildSqlSourceFromStrings(update.value(), parameterType, languageDriver);
        } else if (annotation instanceof Insert insert) {
            return buildSqlSourceFromStrings(insert.value(), parameterType, languageDriver);
        } else if (annotation instanceof Delete delete) {
            return buildSqlSourceFromStrings(delete.value(), parameterType, languageDriver);
        }

        return null;
    }

    private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        return languageDriver.createSqlSource(configuration, String.join(" ", strings).trim(), parameterTypeClass);
    }

    private AnnotationWrapper getAnnotationWrapper(Method method, Collection<Class<? extends Annotation>> targetTypes) {
        for (Class<? extends Annotation> annotationType : targetTypes) {
            Annotation annotation = method.getAnnotation(annotationType);
            if (annotation != null) {
                return new AnnotationWrapper(annotation);
            }
        }
        return null;
    }

    /**
     * 获取方法的返回类型
     */
    private static Class<?> getReturnType(Method method, Class<?> type) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            // 如果为 Array 获取实际类型
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
        }
        else if (resolvedReturnType instanceof ParameterizedType parameterizedType) {
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            // TODO
        }

        return returnType;
    }

    private static class AnnotationWrapper {
        private final Annotation annotation;
        private final SqlCommandType sqlCommandType;

        private final String databaseId = null;

        AnnotationWrapper(Annotation annotation) {
            this.annotation = annotation;
            if (annotation instanceof Select) {
                sqlCommandType = SqlCommandType.SELECT;
            } else if (annotation instanceof Update) {
                sqlCommandType = SqlCommandType.UPDATE;
            } else if (annotation instanceof Insert) {
                sqlCommandType = SqlCommandType.INSERT;
            } else if (annotation instanceof Delete) {
                sqlCommandType = SqlCommandType.DELETE;
            } else {
                sqlCommandType = SqlCommandType.UNKNOWN;
            }
        }

        Annotation getAnnotation() {
            return annotation;
        }

        SqlCommandType getSqlCommandType() {
            return sqlCommandType;
        }

        String getDatabaseId() {
            return databaseId;
        }
    }

}
