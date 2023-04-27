package com.lin.mybatis.exceptions;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:45:50
 */
public class MybatisException extends RuntimeException{


    public MybatisException() {

    }

    public MybatisException(String message) {
        super(message);
    }

    public MybatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisException(Throwable cause) {
        super(cause);
    }

}
