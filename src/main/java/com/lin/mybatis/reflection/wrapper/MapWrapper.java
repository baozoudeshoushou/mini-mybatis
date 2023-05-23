package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.SystemMetaObject;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:27:42
 */
public class MapWrapper extends BaseWrapper {

    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            return getCollectionValue(prop, collection);
        }
        return map.get(prop.getName());
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            setCollectionValue(prop, collection, value);
        }
        else {
            map.put(prop.getName(), value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            }
            else {
                return metaValue.getSetterType(prop.getChildren());
            }
        }
        if (map.get(name) != null) {
            return map.get(name).getClass();
        }
        else {
            return Object.class;
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            }
            else {
                return metaValue.getGetterType(prop.getChildren());
            }
        }
        if (map.get(name) != null) {
            return map.get(name).getClass();
        }
        else {
            return Object.class;
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return map.containsKey(prop.getName());
        }
        if (map.containsKey(prop.getIndexedName())) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return true;
            }
            else {
                return metaValue.hasGetter(prop.getChildren());
            }
        }
        else {
            return false;
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(prop, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(),
                metaObject.getReflectorFactory());
    }

}
