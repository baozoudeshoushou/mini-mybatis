package com.lin.mybatis.scripting.xmltags;

/**
 * SQL 节点
 * @author linjiayi5
 */
public interface SqlNode {

    boolean apply(DynamicContext context);

}
