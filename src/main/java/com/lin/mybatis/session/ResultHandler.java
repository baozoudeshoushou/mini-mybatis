package com.lin.mybatis.session;

/**
 * 结果处理器
 * @author linjiayi5
 */
public interface ResultHandler<T> {

    void handleResult(ResultContext<? extends T> resultContext);

}
