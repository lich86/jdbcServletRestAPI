package chervonnaya;

import chervonnaya.util.TestConnectionManager;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.stream.Stream;

@Testcontainers
public abstract class BaseIntegrationTest {
    @Container
    public static MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(3306);
    private static Connection connection;
    protected static DataSource dataSource;


    @BeforeAll
    static void initContainer(){
        try {
            MY_SQL_CONTAINER.start();

            Startables.deepStart(Stream.of(MY_SQL_CONTAINER)).join();

            String jdbcUrl = MY_SQL_CONTAINER.getJdbcUrl();
            String username = MY_SQL_CONTAINER.getUsername();
            String password = MY_SQL_CONTAINER.getPassword();

            dataSource = TestConnectionManager.getDataSource(jdbcUrl, username, password);

            connection = dataSource.getConnection();

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
