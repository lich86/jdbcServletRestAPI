package chervonnaya.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConnectionManager {

    public static DataSource getDataSource(String jdbcUrl, String username, String password) {
        try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties properties = new Properties();
            if (input == null) {
                throw new IOException("Unable to find db.properties");
            }
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(Integer.parseInt("10"));
            config.setConnectionTimeout(Long.parseLong("30000"));

            return new HikariDataSource(config);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Initialization failed: " + e.getMessage());
        }
    }
}
