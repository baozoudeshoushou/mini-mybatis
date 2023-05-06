package com.lin.mybatis.builder.xml;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
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
public class XMLMapperBuilder {

    private final XPathParser parser;

    protected final Configuration configuration;

    protected String namespace;

    private final String resource;

    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource) {
        this.parser = new XPathParser(reader);;
        this.configuration = configuration;
        this.resource = resource;
    }

    public void parse() {
        configurationElement(parser.evalNode("/mapper"));
        bindMapperForNamespace();
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
            String nodeName = context.getNode().getNodeName();
            SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

            // 先只处理 SELECT
            if (!isSelect) {
                continue;
            }

            String id = context.getStringAttribute("id");
            String parameterType = context.getStringAttribute("parameterType");
            String resultType = context.getStringAttribute("resultType");
            String sql = context.getStringBody();

            Map<Integer, String> parameter = new HashMap<>();
            Pattern pattern = Pattern.compile("(#\\{(.*?)})");
            Matcher matcher = pattern.matcher(sql);
            for (int i = 1; matcher.find(); i++) {
                String g1 = matcher.group(1);
                String g2 = matcher.group(2);
                parameter.put(i, g2);
                sql = sql.replace(g1, "?");
            }

            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setId(namespace + "." + id);
            mappedStatement.setSql(sql);
            mappedStatement.setSqlCommandType(sqlCommandType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameter(parameter);

            configuration.addMappedStatement(mappedStatement);
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
