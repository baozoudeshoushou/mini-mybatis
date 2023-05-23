package com.lin.mybatis.reflection;

import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.property.PropertyTokenizer;
import com.lin.mybatis.reflection.wrapper.*;

import java.util.Collection;
import java.util.Map;

/**
 * 所有对 Object 的访问操作都需要通过这个类
 * @author linjiayi5
 */
public class MetaObject {

    /** 原对象 */
    private Object originalObject;

    /** 对象包装器 */
    private ObjectWrapper objectWrapper;

    /** 对象工厂 */
    private ObjectFactory objectFactory;

    /** 对象包装工厂 */
    private ObjectWrapperFactory objectWrapperFactory;

    private final ReflectorFactory reflectorFactory;

    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;
        this.reflectorFactory = reflectorFactory;

        if (object instanceof ObjectWrapper) {
            this.objectWrapper = (ObjectWrapper) object;
        }
        else if (objectWrapperFactory.hasWrapperFor(object)) {
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        }
        else if (object instanceof Map) {
            this.objectWrapper = new MapWrapper(this, (Map) object);
        }
        else if (object instanceof Collection) {
            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        }
        else {
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        if (object == null) {
            return SystemMetaObject.NULL_META_OBJECT;
        }
        return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }


    /* --------以下方法委派给 ObjectWrapper------ */

    public String findProperty(String propName, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(propName, useCamelCaseMapping);
    }

    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }

    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return objectWrapper.get(prop);
        }
        String indexedName = prop.getIndexedName();
        MetaObject metaValue = metaObjectForProperty(indexedName);
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return null;
        }
        else {
            return metaValue.getValue(prop.getChildren());
        }
    }

    public void setValue(String name, Object value) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null) {
                    // don't instantiate child path if value is null
                    return;
                }
                metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
            }
            metaValue.setValue(prop.getChildren(), value);
        }
        else {
            objectWrapper.set(prop, value);
        }
    }

    public MetaObject metaObjectForProperty(String name) {
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
    }


}
