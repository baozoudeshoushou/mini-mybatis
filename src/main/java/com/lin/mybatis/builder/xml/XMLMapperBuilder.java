package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.builder.MapperBuilderAssistant;
import com.lin.mybatis.builder.ResultMapResolver;
import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.mapping.ResultFlag;
import com.lin.mybatis.mapping.ResultMap;
import com.lin.mybatis.mapping.ResultMapping;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.parsing.XPathParser;
import com.lin.mybatis.session.Configuration;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
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
            // 解析配置
            configurationElement(parser.evalNode("/mapper"));
            // 防止重复加载
            configuration.addLoadedResource(resource);
            // 创建 mapper
            bindMapperForNamespace();
        }
    }

    private void configurationElement(XNode context) {
        String namespace = context.getStringAttribute("namespace");
        if (namespace == null || namespace.isEmpty()) {
            throw new MybatisException("Mapper's namespace cannot be empty");
        }
        builderAssistant.setCurrentNamespace(namespace);
        // 解析 resultMap
        resultMapElements(context.evalNodes("/mapper/resultMap"));
        // 解析 SQL
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    }

    private void buildStatementFromContext(List<XNode> list) {
        for (XNode context : list) {
            XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context);
            statementParser.parseStatementNode();
        }
    }

    private void resultMapElements(List<XNode> list) {
        for (XNode resultMapNode : list) {
            try {
                resultMapElement(resultMapNode);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private ResultMap resultMapElement(XNode resultMapNode) {
        return resultMapElement(resultMapNode, Collections.emptyList(), null);
    }

    private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) {
        String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType",
                resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
        Class<?> typeClass = resolveClass(type);

        List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            List<ResultFlag> flags = new ArrayList<>();
            if ("id".equals(resultChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            ResultMapping resultMapping = buildResultMappingFromContext(resultChild, typeClass, flags);
            resultMappings.add(resultMapping);
        }

        String id = resultMapNode.getStringAttribute("id");
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
        return resultMapResolver.resolve();
    }

    private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
        String property = context.getStringAttribute("property");
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        Class<?> javaTypeClass = resolveClass(javaType);
        return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, flags);
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
