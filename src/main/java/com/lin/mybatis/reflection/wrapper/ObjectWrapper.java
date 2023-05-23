package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.property.PropertyTokenizer;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:25:36
 */
public interface ObjectWrapper {

    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);

    String findProperty(String name, boolean useCamelCaseMapping);

    String[] getGetterNames();

    String[] getSetterNames();

    Class<?> getSetterType(String name);

    Class<?> getGetterType(String name);

    boolean hasSetter(String name);

    boolean hasGetter(String name);

    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

}
