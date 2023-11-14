package com.lin.mybatis.mapping;

/**
 * Represents the content of a mapped statement read from an XML file or an annotation. It creates the SQL that will be
 * passed to the database out of the input parameter received from the user.
 *
 * @author linjiayi5
 */
public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);

}
