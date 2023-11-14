package com.lin.mybatis.scripting.xmltags;

/**
 * 静态文本 SQL 节点
 * @author linjiayi5
 */
public class StaticTextSqlNode implements SqlNode {

    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        context.appendSql(text);
        return true;
    }

}
