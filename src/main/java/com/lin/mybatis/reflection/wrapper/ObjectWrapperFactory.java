package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.reflection.MetaObject;

/**
 * @Author linjiayi5
 * @Date 2023/5/10 16:21:35
 */
public interface ObjectWrapperFactory {

    boolean hasWrapperFor(Object object);

    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
