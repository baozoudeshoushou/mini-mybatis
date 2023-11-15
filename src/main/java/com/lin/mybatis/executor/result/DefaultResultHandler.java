package com.lin.mybatis.executor.result;

import com.lin.mybatis.reflection.factory.ObjectFactory;
import com.lin.mybatis.session.ResultContext;
import com.lin.mybatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linjiayi5
 */
public class DefaultResultHandler<T> implements ResultHandler<T> {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    public DefaultResultHandler(ObjectFactory objectFactory) {
        list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext<? extends T> context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }

}
