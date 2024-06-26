package chervonnaya.dao;

import chervonnaya.TestData;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.exception.DatabaseOperationException;
import chervonnaya.model.Author;
import chervonnaya.dao.mappers.AuthorDBMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class AuthorDAOTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private AuthorDBMapper authorDBMapper;
    private AuthorDAO authorDAO;
    private AutoCloseable closeable;


    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        authorDAO = new AuthorDAO(dataSource);
        FieldUtils.writeField(authorDAO, "authorDBMapper", authorDBMapper, true);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void findById_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(authorDBMapper.map(resultSet)).thenReturn(TestData.AUTHOR1);

        Optional<Author> actualAuthor = authorDAO.findById(1L);

        Assertions.assertTrue(actualAuthor.isPresent());
        Assertions.assertEquals(TestData.AUTHOR1, actualAuthor.get());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findById_Should_Fail() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);

        Optional<Author> result = authorDAO.findById(1L);

        Assertions.assertFalse(result.isPresent());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findAll_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next())
                .thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false)
                .thenReturn(false);
        Mockito.when(authorDBMapper.map(resultSet)).thenReturn(TestData.AUTHOR1).thenReturn(TestData.AUTHOR2);

        Set<Author> authors = authorDAO.findAll();

        Assertions.assertEquals(2, authors.size());
        Assertions.assertTrue(authors.contains(TestData.AUTHOR1));
        Assertions.assertTrue(authors.contains(TestData.AUTHOR2));
    }

    @Test
    public void create_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString(), Mockito.eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);
        Mockito.when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getLong(1)).thenReturn(1L);

        Long authorId = authorDAO.create(TestData.AUTHOR_DTO);

        Assertions.assertEquals(1L, authorId);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.AUTHOR_DTO.getFirstName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.AUTHOR_DTO.getLastName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, TestData.AUTHOR_DTO.getMiddleName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(4, TestData.AUTHOR_DTO.getPenName());
    }

    @Test
    public void update_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        authorDAO.update(1L, TestData.AUTHOR_DTO);

        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(5, 1L);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.AUTHOR_DTO.getFirstName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.AUTHOR_DTO.getLastName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, TestData.AUTHOR_DTO.getMiddleName());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(4, TestData.AUTHOR_DTO.getPenName());
    }

    @Test
    public void delete_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        authorDAO.delete(1L);

        Mockito.verify(preparedStatement, Mockito.times(4)).setLong(1, 1L);
        Mockito.verify(preparedStatement, Mockito.times(3)).executeUpdate();
        Mockito.verify(connection, Mockito.times(1)).commit();
    }

    @Test
    public void delete_Should_FailWhenAuthorNotFound() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(0);

        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.delete(1L));

        Mockito.verify(connection, Mockito.times(1)).rollback();
    }

    @Test
    public void authorDao_Should_ThrowDatabaseOperationException() throws Exception {
        Mockito.when(dataSource.getConnection()).thenThrow(new SQLException());

        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.findById(1L));
        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.findAll());
        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.create(new AuthorDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.update(1L, new AuthorDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> authorDAO.delete(1L));
    }
}
