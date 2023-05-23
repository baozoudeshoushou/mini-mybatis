package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.reflection.MetaObject;
import com.lin.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:28:30
 */
public abstract class BaseWrapper implements ObjectWrapper {

    protected static final Object[] NO_ARGUMENTS = {};

    protected MetaObject metaObject;

    protected BaseWrapper(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    /**
     * 解析集合
     */
    protected Object resolveCollection(PropertyTokenizer prop, Object object) {
        if ("".equals(prop.getName())) {
            return object;
        }
        return metaObject.getValue(prop.getName());
    }

    /**
     * 取集合的值
     * 中括号有2个意思，一个是Map，一个是List或数组
     */
    protected Object getCollectionValue(PropertyTokenizer prop, Object collection) {
        if (collection instanceof Map) {
            return ((Map) collection).get(prop.getIndex());
        }
        int i = Integer.parseInt(prop.getIndex());
        if (collection instanceof List) {
            return ((List) collection).get(i);
        }
        else if (collection instanceof Object[]) {
            return ((Object[]) collection)[i];
        }
        else if (collection instanceof char[]) {
            return ((char[]) collection)[i];
        }
        else if (collection instanceof boolean[]) {
            return ((boolean[]) collection)[i];
        }
        else if (collection instanceof byte[]) {
            return ((byte[]) collection)[i];
        }
        else if (collection instanceof double[]) {
            return ((double[]) collection)[i];
        }
        else if (collection instanceof float[]) {
            return ((float[]) collection)[i];
        }
        else if (collection instanceof int[]) {
            return ((int[]) collection)[i];
        }
        else if (collection instanceof long[]) {
            return ((long[]) collection)[i];
        }
        else if (collection instanceof short[]) {
            return ((short[]) collection)[i];
        }
        else {
            throw new MybatisException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
        }
    }

    protected void setCollectionValue(PropertyTokenizer prop, Object collection, Object value) {
        if (collection instanceof Map) {
            ((Map) collection).put(prop.getIndex(), value);
        }
        else {
            int i = Integer.parseInt(prop.getIndex());
            if (collection instanceof List) {
                ((List) collection).set(i, value);
            }
            else if (collection instanceof Object[]) {
                ((Object[]) collection)[i] = value;
            }
            else if (collection instanceof char[]) {
                ((char[]) collection)[i] = (Character) value;
            }
            else if (collection instanceof boolean[]) {
                ((boolean[]) collection)[i] = (Boolean) value;
            }
            else if (collection instanceof byte[]) {
                ((byte[]) collection)[i] = (Byte) value;
            }
            else if (collection instanceof double[]) {
                ((double[]) collection)[i] = (Double) value;
            }
            else if (collection instanceof float[]) {
                ((float[]) collection)[i] = (Float) value;
            }
            else if (collection instanceof int[]) {
                ((int[]) collection)[i] = (Integer) value;
            }
            else if (collection instanceof long[]) {
                ((long[]) collection)[i] = (Long) value;
            }
            else if (collection instanceof short[]) {
                ((short[]) collection)[i] = (Short) value;
            }
            else {
                throw new MybatisException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
            }
        }
    }

}
