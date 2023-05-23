package com.lin.mybatis.reflection;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.reflection.invoker.*;
import com.lin.mybatis.reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 反射类，解析一个 Class，包括 getter、setter、field。
 *
 * @author linjiayi5
 */
public class Reflector {

    private final Class<?> type;

    private final String[] readablePropertyNames;
    private final String[] writablePropertyNames;

    private final Map<String, Invoker> setMethods = new HashMap<>();
    private final Map<String, Invoker> getMethods = new HashMap<>();
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();

    private final Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    private Constructor<?> defaultConstructor;

    public Reflector(Class<?> clazz) {
        this.type = clazz;
        addDefaultConstructor(clazz);
        Method[] classMethods = getClassMethods(clazz);
        addGetMethods(classMethods);
        addSetMethods(classMethods);
        addFields(clazz);
        this.readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        this.writablePropertyNames = setMethods.keySet().toArray(new String[0]);
        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    private void addDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        Arrays.stream(declaredConstructors)
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny()
                .ifPresent(constructor -> this.defaultConstructor = constructor);
    }

    private void addGetMethods(Method[] methods) {
        Map<String, List<Method>> conflictingGetters = new HashMap<>();
        Arrays.stream(methods)
                .filter(method -> method.getParameterCount() == 0 && PropertyNamer.isGetter(method.getName()))
                .forEach(method -> addMethodConflict(conflictingGetters, PropertyNamer.methodToProperty(method.getName()), method));
        resolveGetterConflicts(conflictingGetters);
    }

    /***
     * 选举 getter
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
            Method winner = null;
            String propName = entry.getKey();
            boolean isAmbiguous = false;
            for (Method candidate : entry.getValue()) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }

                Class<?> winnerType = winner.getReturnType();
                Class<?> candidateType = candidate.getReturnType();
                if (candidateType.equals(winnerType)) {
                    if (!boolean.class.equals(candidateType)) {
                        isAmbiguous = true;
                        break;
                    }
                    if (candidate.getName().startsWith("is")) {
                        winner = candidate;
                    }
                }
                else if (candidateType.isAssignableFrom(winnerType)) {
                    // OK getter type is descendant
                }
                else {
                    isAmbiguous = true;
                    break;
                }
            }

            addGetMethod(propName, winner, isAmbiguous);
        }
    }

    private void addGetMethod(String name, Method method, boolean isAmbiguous) {
        MethodInvoker invoker;
        if (isAmbiguous) {
            invoker = new AmbiguousMethodInvoker(
                    method,
                    MessageFormat.format(
                            "Illegal overloaded getter method with ambiguous type for property ''{0}'' in class ''{1}''. This breaks the JavaBeans specification and can cause unpredictable results.",
                            name, method.getDeclaringClass().getName()
                    )
            );
        }
        else {
            invoker = new MethodInvoker(method);
        }

        getMethods.put(name, invoker);
        getTypes.put(name, method.getReturnType());
    }


    private void addSetMethods(Method[] methods) {
        Map<String, List<Method>> conflictingSetters = new HashMap<>();
        Arrays.stream(methods)
                .filter(method -> method.getParameterCount() == 1 && PropertyNamer.isSetter(method.getName()))
                .forEach(method -> addMethodConflict(conflictingSetters, PropertyNamer.methodToProperty(method.getName()), method));
        resolveSetterConflicts(conflictingSetters);
    }

    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        if (isValidPropertyName(name)) {
            List<Method> methodList = conflictingMethods.computeIfAbsent(name, k -> new ArrayList<>());
            methodList.add(method);
        }
    }

    private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingSetters.entrySet()) {
            String propName = entry.getKey();
            List<Method> setters = entry.getValue();
            Class<?> getterType = getTypes.get(propName);
            boolean isGetterAmbiguous = getMethods.get(propName) instanceof AmbiguousMethodInvoker;
            boolean isSetterAmbiguous = false;
            Method match = null;

            for (Method setter : setters) {
                if (!isGetterAmbiguous && setter.getParameterTypes()[0].equals(getterType)) {
                    // should be the best match
                    match = setter;
                    break;
                }
                if (!isSetterAmbiguous) {
                    match = pickBetterSetter(match, setter, propName);
                    isSetterAmbiguous = match == null;
                }
            }

            if (match != null) {
                addSetMethod(propName, match);
            }
        }
    }

    private Method pickBetterSetter(Method setter1, Method setter2, String property) {
        if (setter1 == null) {
            return setter2;
        }
        Class<?> paramType1 = setter1.getParameterTypes()[0];
        Class<?> paramType2 = setter2.getParameterTypes()[0];
        if (paramType1.isAssignableFrom(paramType2)) {
            return setter2;
        }
        if (paramType2.isAssignableFrom(paramType1)) {
            return setter1;
        }

        MethodInvoker invoker = new AmbiguousMethodInvoker(setter1,
                MessageFormat.format(
                        "Ambiguous setters defined for property ''{0}'' in class ''{1}'' with types ''{2}'' and ''{3}''.", property,
                        setter2.getDeclaringClass().getName(), paramType1.getName(), paramType2.getName())
        );
        setMethods.put(property, invoker);
        setTypes.put(property, setter1.getParameterTypes()[0]);
        return null;
    }

    private void addSetMethod(String name, Method method) {
        MethodInvoker invoker = new MethodInvoker(method);
        setMethods.put(name, invoker);
        setTypes.put(name, method.getParameterTypes()[0]);
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!setMethods.containsKey(field.getName())) {
                int modifiers = field.getModifiers();
                // removed the check for final because JDK 1.5 allows modification of final fields through reflection (JSR-133).
                if (!Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers)) {
                    addSetField(field);
                }
            }
            if (!getMethods.containsKey(field.getName())) {
                addGetField(field);
            }
        }
        // 递归父类
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMethods.put(field.getName(), new SetFieldInvoker(field));
            setTypes.put(field.getName(), field.getType());
        }
    }

    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMethods.put(field.getName(), new GetFieldInvoker(field));
            getTypes.put(field.getName(), field.getType());
        }
    }

    private boolean isValidPropertyName(String name) {
        return (!name.startsWith("$") && !"serialVersionUID".equals(name) && !"class".equals(name));
    }

    private Method[] getClassMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
            // we also need to look for interface methods, because the class may be abstract
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();
        return methods.toArray(new Method[0]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method method : methods) {
            if (!method.isBridge()) {
                String signature = getSignature(method);
                // check to see if the method is already known
                // if it is known, then an extended class must have overridden a method
                if (!uniqueMethods.containsKey(signature)) {
                    uniqueMethods.put(signature, method);
                }
            }
        }
    }

    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        sb.append(returnType.getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(i == 0 ? ':' : ',').append(parameters[i].getName());
        }
        return sb.toString();
    }

    /**
     * Gets the name of the class the instance provides information for.
     * @return The class name
     */
    public Class<?> getType() {
        return type;
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = setMethods.get(propertyName);
        if (method == null) {
            throw new MybatisException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = getMethods.get(propertyName);
        if (method == null) {
            throw new MybatisException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    /**
     * Gets the type for a property setter.
     * @param propertyName the name of the property
     * @return The Class of the property setter
     */
    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new MybatisException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /**
     * Gets the type for a property getter.
     * @param propertyName the name of the property
     * @return The Class of the property getter
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> clazz = getTypes.get(propertyName);
        if (clazz == null) {
            throw new MybatisException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /**
     * Gets an array of the readable properties for an object.
     * @return The array
     */
    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    /**
     * Gets an array of the writable properties for an object.
     * @return The array
     */
    public String[] getSetablePropertyNames() {
        return writablePropertyNames;
    }

    /**
     * Check to see if a class has a writable property by name.
     * @param propertyName the name of the property to check
     * @return True if the object has a writable property by the name
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.containsKey(propertyName);
    }

    /**
     * Check to see if a class has a readable property by name.
     * @param propertyName the name of the property to check
     * @return True if the object has a readable property by the name
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.containsKey(propertyName);
    }

    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

}
