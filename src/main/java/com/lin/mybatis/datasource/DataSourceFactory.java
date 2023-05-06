package com.lin.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author linjiayi5
 */
public interface DataSourceFactory {

    void setProperties(Properties props);

    DataSource getDataSource();

}
