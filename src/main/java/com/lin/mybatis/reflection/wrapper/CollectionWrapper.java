package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.property.PropertyTokenizer;

import java.util.Collection;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:30:52
 */
public class CollectionWrapper implements ObjectWrapper {

    private final Collection<Object> object;

    public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
        this.object = object;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

}
