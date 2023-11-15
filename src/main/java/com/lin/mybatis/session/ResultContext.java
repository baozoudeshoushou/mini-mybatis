package com.lin.mybatis.session;

/**
 * @author linjiayi5
 */
public interface ResultContext<T> {

    T getResultObject();

    int getResultCount();

    boolean isStopped();

    void stop();

}
