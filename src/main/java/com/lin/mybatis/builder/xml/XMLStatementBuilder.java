package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.session.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author linjiayi5
 * @Date 2023/5/23 16:47:35
 */
public class XMLStatementBuilder extends BaseBuilder {

    private final XNode context;

    private final String namespace;

    public XMLStatementBuilder(Configuration configuration, XNode context, String namespace) {
        super(configuration);
        this.context = context;
        this.namespace = namespace;
    }

    public void parseStatementNode() {
        String id = context.getStringAttribute("id");
        String databaseId = context.getStringAttribute("databaseId");

        String nodeName = context.getNode().getNodeName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

        // 先只处理 SELECT
        if (!isSelect) {
            return;
        }

        // 参数类型
        String parameterType = context.getStringAttribute("parameterType");
        Class<?> parameterTypeClass = resolveClass(parameterType);

        // 获取默认语言驱动器
        String lang = context.getStringAttribute("lang");
        LanguageDriver langDriver = getLanguageDriver(lang);

        // 结果类型
        String resultType = context.getStringAttribute("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);

        SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);

        String msId = namespace + "." + id;

        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType, resultTypeClass).build();

        configuration.addMappedStatement(mappedStatement);
    }

    private LanguageDriver getLanguageDriver(String lang) {
        Class<? extends LanguageDriver> langClass = null;
        if (lang != null) {
            langClass = resolveClass(lang);
        }
        return configuration.getLanguageDriver(langClass);
    }

}
