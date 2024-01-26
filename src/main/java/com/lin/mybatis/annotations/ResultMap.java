package com.lin.mybatis.annotations;

import java.lang.annotation.*;

/**
 * @author linjiayi5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultMap {

    String[] value();

}
