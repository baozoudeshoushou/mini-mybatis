package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.datasource.DataSourceFactory;
import com.lin.mybatis.datasource.HikariDatasourceFactory;
import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.mapping.Environment;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.parsing.XPathParser;
import com.lin.mybatis.session.Configuration;
import com.lin.mybatis.transaction.TransactionFactory;
import com.lin.mybatis.type.TypeAliasRegistry;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.Properties;

/**
 * The class for parsing xml configuration
 *
 * @Author linjiayi5
 * @Date 2023/4/26 16:35:29
 */
public class XMLConfigBuilder extends BaseBuilder {

    private boolean parsed;

    private final XPathParser parser;

    private String environment;

    protected final TypeAliasRegistry typeAliasRegistry;

    public XMLConfigBuilder(Reader reader) {
        this(reader, null);
    }

    public XMLConfigBuilder(Reader reader, String environment) {
        super(new Configuration());
        this.parsed = false;
        this.parser = new XPathParser(reader);
        this.environment = environment;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
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
                TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                DataSourceFactory dataSourceFactory = dataSourceElement(child.evalNode("dataSource"));
                DataSource dataSource = dataSourceFactory.getDataSource();
                Environment env = new Environment(id, txFactory, dataSource);
                this.configuration.setEnvironment(env);
            }
        }
    }

    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        String type = context.getStringAttribute("type");
        Properties props = context.getChildrenAsProperties();
        TransactionFactory factory = (TransactionFactory) resolveAlias(type).getDeclaredConstructor().newInstance();
        factory.setProperties(props);
        return factory;
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        String type = context.getStringAttribute("type");
        Properties properties = context.getChildrenAsProperties();
        DataSourceFactory datasourceFactory = (DataSourceFactory) resolveAlias(type).getDeclaredConstructor().newInstance();
        datasourceFactory.setProperties(properties);
        return datasourceFactory;
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

        for (XNode child : parent.getChildren()) {
            String resource = child.getStringAttribute("resource");
            if (resource != null) {
                Reader reader = Resources.getResourceAsReader(resource);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(reader, configuration, resource);
                xmlMapperBuilder.parse();
            }
        }
    }

    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

}
