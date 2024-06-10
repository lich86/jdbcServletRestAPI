package chervonnaya.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class TestConnectionManager {
    public static DataSource getDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(Integer.parseInt("10"));
        config.setConnectionTimeout(Long.parseLong("30000"));

        return new HikariDataSource(config);
    }
}
