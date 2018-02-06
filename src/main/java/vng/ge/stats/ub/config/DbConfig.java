package vng.ge.stats.ub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by canhtq on 15/09/2017.
 */
@Configuration
public class DbConfig {

    private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);

    @Bean(name = "dsUserProfile")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource ubDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcUserProfile")
    public JdbcTemplate jdbcTemplate(@Qualifier("dsUserProfile") DataSource dsMySQL) {
        return new JdbcTemplate(dsMySQL);
    }



}
