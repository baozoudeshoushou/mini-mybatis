package com.lin.mybatis.reflection;

import com.lin.mybatis.reflection.factory.DefaultObjectFactory;
import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.lin.mybatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @author linjiayi5
 */
public class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();

    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    public static final MetaObject NULL_META_OBJECT =
            MetaObject.forObject(new NullObject(), DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    private SystemMetaObject() {

    }

    private static class NullObject {

    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }

}
