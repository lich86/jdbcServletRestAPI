package chervonnaya;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

@Testcontainers
public abstract class BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    @Container
    public static MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(3306);

    private static Connection connection;


    @BeforeAll
    static void initContainer(){
        try {
            logger.debug("Starting test containers");
            MY_SQL_CONTAINER.start();

            String jdbcUrl = MY_SQL_CONTAINER.getJdbcUrl();
            String username = MY_SQL_CONTAINER.getUsername();
            String password = MY_SQL_CONTAINER.getPassword();

            connection = DriverManager.getConnection(jdbcUrl, username, password);

            logger.debug("Testcontainer started");

            Database database = new liquibase.database.core.MySQLDatabase();
            database.setConnection(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.sql", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());

        } catch (Exception e) {
            throw new RuntimeException("Error starting test container");
        }
    }

    @AfterAll
    static void stopContainer() throws Exception {
        if (connection != null) {
            connection.close();
        }
        MY_SQL_CONTAINER.stop();
    }


}
