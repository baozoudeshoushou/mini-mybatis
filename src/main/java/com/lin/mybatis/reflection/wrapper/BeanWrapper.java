package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.reflection.MetaClass;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.SystemMetaObject;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.invoker.Invoker;
import com.lin.mybatis.reflection.property.PropertyTokenizer;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:32:20
 */
public class BeanWrapper extends BaseWrapper {

    private final Object object;

    private final MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject);
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass(), metaObject.getReflectorFactory());
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, object);
            return getCollectionValue(prop, collection);
        }
        return getBeanProperty(prop, object);
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, object);
            setCollectionValue(prop, collection, value);
        }
        else {
            setBeanProperty(prop, object, value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findProperty(name, useCamelCaseMapping);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.getSetterType(name);
        }
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return metaClass.getSetterType(name);
        }
        else {
            return metaValue.getSetterType(prop.getChildren());
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.getGetterType(name);
        }
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return metaClass.getGetterType(name);
        } else {
            return metaValue.getGetterType(prop.getChildren());
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.hasSetter(name);
        }
        if (metaClass.hasSetter(prop.getIndexedName())) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.hasSetter(name);
            } else {
                return metaValue.hasSetter(prop.getChildren());
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.hasGetter(name);
        }
        if (metaClass.hasGetter(prop.getIndexedName())) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.hasGetter(name);
            } else {
                return metaValue.hasGetter(prop.getChildren());
            }
        } else {
            return false;
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        MetaObject metaValue;
        Class<?> type = getSetterType(prop.getName());
        Object newObject = objectFactory.create(type);
        metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
        set(prop, newObject);
        return metaValue;
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        Invoker invoker = metaClass.getGetInvoker(prop.getName());
        try {
            return invoker.invoke(object, NO_ARGUMENTS);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        Invoker invoker = metaClass.getSetInvoker(prop.getName());
        Object[] params = { value };
        try {
            invoker.invoke(object, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
