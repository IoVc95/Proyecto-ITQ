package edu.valle.soap.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SupplierDataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String jdbcUrl,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName) {

        return createDataSource(jdbcUrl, username, password, driverClassName);
    }

    @Bean(name = "supplierDataSource")
    public DataSource supplierDataSource(
            @Value("${supplier.datasource.jdbc-url}") String jdbcUrl,
            @Value("${supplier.datasource.username}") String username,
            @Value("${supplier.datasource.password}") String password,
            @Value("${supplier.datasource.driver-class-name}") String driverClassName) {

        return createDataSource(jdbcUrl, username, password, driverClassName);
    }

    @Bean(name = "supplierJdbcTemplate")
    public JdbcTemplate supplierJdbcTemplate(
            @Qualifier("supplierDataSource") DataSource supplierDataSource) {
        return new JdbcTemplate(supplierDataSource);
    }

    private DataSource createDataSource(
            String jdbcUrl,
            String username,
            String password,
            String driverClassName) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
