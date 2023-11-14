package com.lin.mybatis.scripting.xmltags;

import com.lin.mybatis.executor.parameter.ParameterHandler;
import com.lin.mybatis.mapping.BoundSql;
import com.lin.mybatis.mapping.MappedStatement;
import com.lin.mybatis.mapping.SqlSource;
import com.lin.mybatis.parsing.XNode;
import com.lin.mybatis.scripting.LanguageDriver;
import com.lin.mybatis.scripting.defaults.DefaultParameterHandler;
import com.lin.mybatis.session.Configuration;

/**
 * @author linjiayi5
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // TODO 重载方法，不紧要
        return null;
    }

}
