package chervonnaya.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class ConnectionManager {
    private static final HikariDataSource dataSource;

    static {
        try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties properties = new Properties();
            if (input == null) {
                throw new IOException("Unable to find properties");
            }
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.maximumPoolSize")));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.connectionTimeout")));

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Initialization failed: " + e.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }


    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}

