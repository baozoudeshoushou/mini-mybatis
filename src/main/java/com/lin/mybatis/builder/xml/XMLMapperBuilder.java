package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.parsing.XPathParser;
import com.lin.mybatis.session.Configuration;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author linjiayi5
 * @Date 2023/5/5 20:04:36
 */
public class XMLMapperBuilder extends BaseBuilder {

    private final XPathParser parser;

    protected String namespace;

    private final String resource;

    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource) {
        super(configuration);
        this.parser = new XPathParser(reader);
        this.resource = resource;
    }

    public void parse() {
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(parser.evalNode("/mapper"));
            // 防止重复加载
            configuration.addLoadedResource(resource);
            bindMapperForNamespace();
        }
    }

    private void configurationElement(XNode context) {
        String namespace = context.getStringAttribute("namespace");
        if (namespace == null || namespace.isEmpty()) {
            throw new MybatisException("Mapper's namespace cannot be empty");
        }
        this.namespace = namespace;
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    }

    private void buildStatementFromContext(List<XNode> list) {
        for (XNode context : list) {
            XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, context, namespace);
            statementParser.parseStatementNode();
        }
    }

    /**
     * 为该 namespace 创建 mapper
     */
    private void bindMapperForNamespace() {
        Class<?> boundType = null;
        try {
            boundType = Resources.classForName(namespace);
        }
        catch (ClassNotFoundException e) {
            // ignore, bound type is not required
        }

        if (boundType != null && !configuration.hasMapper(boundType)) {
            configuration.addMapper(boundType);
        }
    }

}
