package com.lin.mybatis.builder.xml;

import com.lin.mybatis.builder.BaseBuilder;
import com.lin.mybatis.builder.MapperBuilderAssistant;
import com.lin.mybatis.mapping.SqlCommandType;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.session.Configuration;

import java.util.Locale;

/**
 * @author linjiayi5
 */
public class XMLStatementBuilder extends BaseBuilder {

    private final MapperBuilderAssistant builderAssistant;

    private final XNode context;

    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context) {
        super(configuration);
        this.builderAssistant = builderAssistant;
        this.context = context;
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

        String resultMap = context.getStringAttribute("resultMap");

        // 解析成 SqlSource. DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);

        builderAssistant.addMappedStatement(id, sqlSource, sqlCommandType,
                parameterTypeClass, resultMap, resultTypeClass, databaseId, langDriver);
    }

    private LanguageDriver getLanguageDriver(String lang) {
        Class<? extends LanguageDriver> langClass = null;
        if (lang != null) {
            langClass = resolveClass(lang);
        }
        return configuration.getLanguageDriver(langClass);
    }

}
