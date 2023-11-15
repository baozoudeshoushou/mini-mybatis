package com.lin.mybatis.executor.result;

import com.lin.mybatis.session.ResultContext;

/**
 * @author linjiayi5
 */
public class DefaultResultContext<T> implements ResultContext<T> {

    private T resultObject;

    private int resultCount;

    private boolean stopped;

    @Override
    public T getResultObject() {
        return resultObject;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    public void nextResultObject(T resultObject) {
        resultCount++;
        this.resultObject = resultObject;
    }

}
