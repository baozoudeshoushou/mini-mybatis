package com.lin.mybatis.reflection.factory;

import com.lin.mybatis.exceptions.MybatisException;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linjiayi5
 */
public class DefaultObjectFactory implements ObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        return create(type, null, null);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        Class<?> classToCreate = resolveInterface(type);
        // we know types are assignable
        return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    }

    private <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        try {
            Constructor<T> constructor;
            if (constructorArgTypes == null || constructorArgs == null) {
                constructor = type.getDeclaredConstructor();
                return constructor.newInstance();
            }

            constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[0]));
            return constructor.newInstance(constructorArgs.toArray(new Object[0]));
        }
        catch (Exception e) {
            String argTypes = Optional.ofNullable(constructorArgTypes).orElseGet(Collections::emptyList).stream()
                    .map(Class::getSimpleName).collect(Collectors.joining(","));
            String argValues = Optional.ofNullable(constructorArgs).orElseGet(Collections::emptyList).stream()
                    .map(String::valueOf).collect(Collectors.joining(","));
            throw new MybatisException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values (" + argValues + "). Cause: " + e, e);
        }
    }

    protected Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            classToCreate = ArrayList.class;
        }
        else if (type == Map.class) {
            classToCreate = HashMap.class;
        }
        else if (type == SortedSet.class) {
            // issue #510 Collections Support
            classToCreate = TreeSet.class;
        }
        else if (type == Set.class) {
            classToCreate = HashSet.class;
        }
        else {
            classToCreate = type;
        }
        return classToCreate;
    }


    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
