package com.lin.mybatis.annotations;

import java.lang.annotation.*;

/**
 * @author linjiayi5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {

    /**
     * Returns an SQL for updating record(s).
     * @return an SQL for updating record(s)
     */
    String[] value();

}
