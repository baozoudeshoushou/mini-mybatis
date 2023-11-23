package com.lin.mybatis.reflection;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * @author linjiayi5
 */
public class TypeParameterResolver {

    private TypeParameterResolver() {
    }

    /**
     * Resolve field type.
     * @param field the field
     * @param srcType the src type
     * @return The field type as {@link Type}. If it has type parameters in the declaration,<br>
     *         they will be resolved to the actual runtime {@link Type}s.
     */
    public static Type resolveFieldType(Field field, Type srcType) {
        Type fieldType = field.getGenericType();
        Class<?> declaringClass = field.getDeclaringClass();
        return resolveType(fieldType, srcType, declaringClass);
    }

    /**
     * Resolve return type.
     * @param method the method
     * @param srcType the src type
     * @return The return type of the method as {@link Type}. If it has type parameters in the declaration,<br>
     *         they will be resolved to the actual runtime {@link Type}s.
     */
    public static Type resolveReturnType(Method method, Type srcType) {
        Type returnType = method.getGenericReturnType();
        Class<?> declaringClass = method.getDeclaringClass();
        return resolveType(returnType, srcType, declaringClass);
    }

    /**
     * Resolve param types.
     * @param method the method
     * @param srcType the src type
     * @return The parameter types of the method as an array of {@link Type}s. If they have type parameters in the
     *         declaration,<br>
     *         they will be resolved to the actual runtime {@link Type}s.
     */
    public static Type[] resolveParamTypes(Method method, Type srcType) {
        Type[] paramTypes = method.getGenericParameterTypes();
        Class<?> declaringClass = method.getDeclaringClass();
        Type[] result = new Type[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            result[i] = resolveType(paramTypes[i], srcType, declaringClass);
        }
        return result;
    }

    private static Type resolveType(Type type, Type srcType, Class<?> declaringClass) {
        if (type instanceof TypeVariable) {
            // 泛型，如 T
            return resolveTypeVar((TypeVariable<?>) type, srcType, declaringClass);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            // 参数化类型的整体，如 List<String>，List<? extends N>
            return resolveParameterizedType(parameterizedType, srcType, declaringClass);
        }
        if (type instanceof GenericArrayType) {
            // 泛型数组类型
            return resolveGenericArrayType((GenericArrayType) type, srcType, declaringClass);
        }
        // 普通类型
        return type;
    }

    private static Type resolveGenericArrayType(GenericArrayType genericArrayType, Type srcType, Class<?> declaringClass) {
        // 获取 componentType，例如 N[]，componentType 就是 N
        Type componentType = genericArrayType.getGenericComponentType();
        Type resolvedComponentType = null;
        if (componentType instanceof TypeVariable) {
            // N[]
            resolvedComponentType = resolveTypeVar((TypeVariable<?>) componentType, srcType, declaringClass);
        }
        else if (componentType instanceof GenericArrayType) {
            // N[][] 多维数组
            resolvedComponentType = resolveGenericArrayType((GenericArrayType) componentType, srcType, declaringClass);
        }
        else if (componentType instanceof ParameterizedType) {
            // List<N>[]
            resolvedComponentType = resolveParameterizedType((ParameterizedType) componentType, srcType, declaringClass);
        }

        if (resolvedComponentType instanceof Class<?>) {
            return Array.newInstance((Class<?>) resolvedComponentType, 0).getClass();
        }

        return new GenericArrayTypeImpl(resolvedComponentType);
    }

    private static ParameterizedType resolveParameterizedType(ParameterizedType parameterizedType, Type srcType, Class<?> declaringClass) {
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
        Type[] args = new Type[actualTypeArgs.length];
        for (int i = 0; i < actualTypeArgs.length; i++) {
            if (actualTypeArgs[i] instanceof TypeVariable type) {
                // T
                args[i] = resolveTypeVar(type, srcType, declaringClass);
            }
            else if (actualTypeArgs[i] instanceof ParameterizedType type) {
                // List<T>  List<? extends N>
                args[i] = resolveParameterizedType(type, srcType, declaringClass);
            }
            else if (actualTypeArgs[i] instanceof WildcardType type) {
                // ? extends N
                args[i] = resolveWildcardType(type, srcType, declaringClass);
            }
            else {
                args[i] = actualTypeArgs[i];
            }
        }

        return new ParameterizedTypeImpl(rawType, null, args);
    }

    private static Type resolveWildcardType(WildcardType wildcardType, Type srcType, Class<?> declaringClass) {
        Type[] lowerBounds = resolveWildcardTypeBounds(wildcardType.getLowerBounds(), srcType, declaringClass);
        Type[] upperBounds = resolveWildcardTypeBounds(wildcardType.getUpperBounds(), srcType, declaringClass);
        return new WildcardTypeImpl(lowerBounds, upperBounds);
    }

    private static Type[] resolveWildcardTypeBounds(Type[] bounds, Type srcType, Class<?> declaringClass) {
        Type[] result = new Type[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i] instanceof TypeVariable type) {
                result[i] = resolveTypeVar(type, srcType, declaringClass);
            }
            else if (bounds[i] instanceof ParameterizedType type) {
                result[i] = resolveParameterizedType(type, srcType, declaringClass);
            }
            else if (bounds[i] instanceof WildcardType type) {
                result[i] = resolveWildcardType(type, srcType, declaringClass);
            }
            else {
                result[i] = bounds[i];
            }
        }
        return result;
    }

    private static Type resolveTypeVar(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass) {
        Type result;
        Class<?> clazz;
        if (srcType instanceof Class<?> type) {
            // 一般都是 Class
            clazz = type;
        }
        else if (srcType instanceof ParameterizedType type) {
            clazz = (Class<?>) type.getRawType();
        }
        else {
            throw new IllegalArgumentException("The 2nd arg must be Class or ParameterizedType, but was: " + srcType.getClass());
        }

        if (clazz == declaringClass) {
            Type[] bounds = typeVar.getBounds();
            if (bounds.length > 0) {
                return bounds[0];
            }
            return Object.class;
        }

        // 获取超类的类型，包括准确的实际类型参数
        Type superclass = clazz.getGenericSuperclass();
        result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superclass);
        if (result != null) {
            return result;
        }

        // 获取接口的类型，包括准确的实际类型参数
        Type[] superInterfaces = clazz.getGenericInterfaces();
        for (Type superInterface : superInterfaces) {
            result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superInterface);
            if (result != null) {
                return result;
            }
        }

        return Object.class;
    }

    private static Type scanSuperTypes(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass, Class<?> clazz, Type superclass) {
        // interface Level1Mapper<E, F> extends Level0Mapper<E, F, String>
        // interface Level0Mapper<L, M, N>
        // typeVar = N, srcType = Level1Mapper, declaringClass = Level0Mapper
        // clazz = Level1Mapper , superclass = Level0Mapper<E,F,String>
        if (superclass instanceof ParameterizedType parentAsType) {
            Class<?> parentAsClass = (Class<?>) parentAsType.getRawType();
            // 超类参数化类型，这里是 [L,M,N]
            TypeVariable<?>[] parentTypeVars = parentAsClass.getTypeParameters();
            if (srcType instanceof ParameterizedType) {
                parentAsType = translateParentTypeVars((ParameterizedType) srcType, clazz, parentAsType);
            }
            // 如果声明该方法的类就是和 parentAsClass 是同一个
            if (declaringClass == parentAsClass) {
                for (int i = 0; i < parentTypeVars.length; i++) {
                    if (typeVar.equals(parentTypeVars[i])) {
                        // 如果参数相同，返回真实类型。这里是 String
                        return parentAsType.getActualTypeArguments()[i];
                    }
                }
            }
            // 如果声明该方法的类还是 parentAsClass 父类，递归查找
            if (declaringClass.isAssignableFrom(parentAsClass)) {
                return resolveTypeVar(typeVar, parentAsType, declaringClass);
            }
        }
        else if (superclass instanceof Class<?> && declaringClass.isAssignableFrom((Class<?>) superclass)) {
            return resolveTypeVar(typeVar, superclass, declaringClass);
        }
        return null;
    }

    private static ParameterizedType translateParentTypeVars(ParameterizedType srcType, Class<?> srcClass, ParameterizedType parentType) {
        Type[] parentTypeArgs = parentType.getActualTypeArguments();
        Type[] srcTypeArgs = srcType.getActualTypeArguments();
        TypeVariable<?>[] srcTypeVars = srcClass.getTypeParameters();
        Type[] newParentArgs = new Type[parentTypeArgs.length];
        boolean noChange = true;
        for (int i = 0; i < parentTypeArgs.length; i++) {
            if (parentTypeArgs[i] instanceof TypeVariable) {
                for (int j = 0; j < srcTypeVars.length; j++) {
                    if (srcTypeVars[j].equals(parentTypeArgs[i])) {
                        noChange = false;
                        newParentArgs[i] = srcTypeArgs[j];
                    }
                }
            } else {
                newParentArgs[i] = parentTypeArgs[i];
            }
        }
        return noChange ? parentType : new ParameterizedTypeImpl((Class<?>) parentType.getRawType(), null, newParentArgs);
    }

    static class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> rawType;

        private final Type ownerType;

        private final Type[] actualTypeArguments;

        public ParameterizedTypeImpl(Class<?> rawType, Type ownerType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public String toString() {
            return "ParameterizedTypeImpl [rawType=" + rawType + ", ownerType=" + ownerType + ", actualTypeArguments="
                    + Arrays.toString(actualTypeArguments) + "]";
        }
    }

    static class WildcardTypeImpl implements WildcardType {
        private final Type[] lowerBounds;

        private final Type[] upperBounds;

        WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }

    }

    static class GenericArrayTypeImpl implements GenericArrayType {
        private final Type genericComponentType;

        GenericArrayTypeImpl(Type genericComponentType) {
            this.genericComponentType = genericComponentType;
        }

        @Override
        public Type getGenericComponentType() {
            return genericComponentType;
        }

    }

}
