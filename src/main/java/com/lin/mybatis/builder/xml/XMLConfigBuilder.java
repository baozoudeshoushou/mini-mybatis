package com.lin.mybatis.builder.xml;

import com.lin.mybatis.datasource.SimpleDataSource;
import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.parsing.XPathParser;
import com.lin.mybatis.session.Configuration;

import java.io.Reader;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class for parsing xml configuration
 *
 * @Author linjiayi5
 * @Date 2023/4/26 16:35:29
 */
public class XMLConfigBuilder {

    private boolean parsed;

    private final XPathParser parser;

    protected final Configuration configuration;

    private String environment;

    public XMLConfigBuilder(Reader reader) {
        this(reader, null);
    }

    public XMLConfigBuilder(Reader reader, String environment) {
        this.parsed = false;
        this.parser = new XPathParser(reader);
        this.configuration = new Configuration();
        this.environment = environment;
    }

    public Configuration parse() {
        if (parsed) {
            throw new MybatisException("Each XMLConfigBuilder can only be used once.");
        }

        XNode xNode = parser.evalNode("/configuration");
        parseConfiguration(xNode);

        return this.configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            environmentsElement(root.evalNode("environments"));
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new MybatisException("Error parsing SQL Mapper Configuration", e);
        }
    }

    private void environmentsElement(XNode context) throws Exception {
        if (context == null) {
            return;
        }

        if (environment == null) {
            environment = context.getStringAttribute("default");
        }

        for (XNode child : context.getChildren()) {
            String id = child.getStringAttribute("id");
            if (isSpecifiedEnvironment(id)) {
                Properties datasourceProperties = dataSourceElement(child.evalNode("dataSource"));
                SimpleDataSource simpleDataSource = new SimpleDataSource(datasourceProperties);
                Environment env = new Environment(id, simpleDataSource);
                this.configuration.setEnvironment(env);
            }
        }

    }

    private Properties dataSourceElement(XNode context) throws Exception {
        String type = context.getStringAttribute("type");
        Properties properties = context.getChildrenAsProperties();
        return properties;
    }

    /**
     * 判断是否是当前 env
     * @param id envId
     */
    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new MybatisException("No environment specified.");
        }
        if (id == null) {
            throw new MybatisException("Environment requires an id attribute.");
        }
        return environment.equals(id);
    }

    private void mapperElement(XNode parent) throws Exception {
        if (parent == null) {
            return;
        }

        Map<String, XNode> map = new HashMap<>();

        for (XNode child : parent.getChildren()) {
            String resource = child.getStringAttribute("resource");
            Reader reader = Resources.getResourceAsReader(resource);
            XPathParser xPathParser = new XPathParser(reader);

            XNode root = xPathParser.evalNode("/mapper");
            // namespace
            String namespace = root.getStringAttribute("namespace");

            // select
            List<XNode> selectNodes = root.evalNodes("select");
            for (XNode selectNode : selectNodes) {
                String id = selectNode.getStringAttribute("id");
                String parameterType = selectNode.getStringAttribute("parameterType");
                String resultType = selectNode.getStringAttribute("resultType");
                String sql = selectNode.getStringBody();

                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);

            }


        }
    }

}
