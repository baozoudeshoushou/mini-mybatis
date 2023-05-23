package com.lin.mybatis.reflection.wrapper;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.reflection.MetaObject;

/**
 * @author linjiayi5
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new MybatisException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
