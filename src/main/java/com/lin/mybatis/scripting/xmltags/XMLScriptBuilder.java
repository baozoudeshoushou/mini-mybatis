package com.lin.mybatis.scripting.xmltags;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.scripting.defaults.RawSqlSource;
import com.lin.mybatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linjiayi5
 */
public class XMLScriptBuilder extends BaseBuilder {

    private final XNode context;

    private final Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration, XNode context, Class<?> parameterType) {
        super(configuration);
        this.context = context;
        this.parameterType = parameterType;
    }

    public SqlSource parseScriptNode() {
        MixedSqlNode rootSqlNode = parseDynamicTags(context);
        return new RawSqlSource(configuration, rootSqlNode, parameterType);
    }

    protected MixedSqlNode parseDynamicTags(XNode node) {
        List<SqlNode> contents = new ArrayList<>();
        // SQL
        String data = node.getStringBody();
        contents.add(new StaticTextSqlNode(data));
        return new MixedSqlNode(contents);
    }

}
