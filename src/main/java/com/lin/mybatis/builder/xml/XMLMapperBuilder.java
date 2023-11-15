package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.builder.MapperBuilderAssistant;
import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.parsing.XPathParser;
import com.lin.mybatis.session.Configuration;

import java.io.Reader;
import java.util.List;

/**
 * @author linjiayi5
 */
public class XMLMapperBuilder extends BaseBuilder {

    private final XPathParser parser;

    private final MapperBuilderAssistant builderAssistant;

    private final String resource;

    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
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
        builderAssistant.setCurrentNamespace(namespace);
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    }

    private void buildStatementFromContext(List<XNode> list) {
        for (XNode context : list) {
            XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context);
            statementParser.parseStatementNode();
        }
    }

    /**
     * 为该 namespace 创建 mapper
     */
    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace != null) {
            Class<?> boundType = null;
            try {
                boundType = Resources.classForName(namespace);
            }
            catch (ClassNotFoundException e) {
                // ignore, bound type is not required
            }

            if (boundType != null && !configuration.hasMapper(boundType)) {
                configuration.addMapper(boundType);
                configuration.addLoadedResource("namespace:" + namespace);
            }
        }
    }

}
