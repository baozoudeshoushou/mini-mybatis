package com.lin.mybatis.session;

import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.parsing.XNode;

import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:26:16
 */
public class Configuration {

    protected Environment environment;

    protected Map<String, XNode> mapperElement;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setMapperElement(Map<String, XNode> mapperElement) {
        this.mapperElement = mapperElement;
    }

}
