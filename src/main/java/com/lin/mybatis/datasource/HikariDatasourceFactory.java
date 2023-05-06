package com.lin.mybatis.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author linjiayi5
 */
public class HikariDatasourceFactory implements DataSourceFactory {

    private Properties props;

    private DataSource dataSource;

    @Override
    public void setProperties(Properties props) {
        this.props = props;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("url"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
