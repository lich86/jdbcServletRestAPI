package chervonnaya.dao;

import chervonnaya.TestData;
import chervonnaya.dto.BookDTO;
import chervonnaya.exception.DatabaseOperationException;
import chervonnaya.model.Book;
import chervonnaya.dao.mappers.BookDBMapper;
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

public class BookDAOTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private BookDBMapper bookDBMapper;
    private BookDAO bookDAO;
    private AutoCloseable closeable;


    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        bookDAO = new BookDAO(dataSource);
        FieldUtils.writeField(bookDAO, "bookDBMapper", bookDBMapper, true);
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
        Mockito.when(bookDBMapper.map(resultSet)).thenReturn(TestData.BOOK1);

        Optional<Book> actualBook = bookDAO.findById(1L);

        Assertions.assertTrue(actualBook.isPresent());
        Assertions.assertEquals(TestData.BOOK1, actualBook.get());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findById_Should_Fail() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);

        Optional<Book> result = bookDAO.findById(1L);

        Assertions.assertFalse(result.isPresent());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findAll_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next())
                .thenReturn(true).thenReturn(false).thenReturn(false)
                .thenReturn(true).thenReturn(false).thenReturn(false)
                .thenReturn(false);
        Mockito.when(bookDBMapper.map(resultSet)).thenReturn(TestData.BOOK1).thenReturn(TestData.BOOK2);

        Set<Book> books = bookDAO.findAll();

        Assertions.assertEquals(2, books.size());
        Assertions.assertTrue(books.contains(TestData.BOOK1));
        Assertions.assertTrue(books.contains(TestData.BOOK2));
    }

    @Test
    public void create_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString(), Mockito.eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);
        Mockito.when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getLong(1)).thenReturn(1L);

        Long bookId = bookDAO.create(TestData.BOOK_DTO);

        Assertions.assertEquals(1L, bookId);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.BOOK_DTO.getOriginalTitle());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.BOOK_DTO.getOriginalLanguage().name());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, TestData.BOOK_DTO.getDescription());
    }

    @Test
    public void update_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        bookDAO.update(1L, TestData.BOOK_DTO);

        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(4, 1L);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.BOOK_DTO.getOriginalTitle());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.BOOK_DTO.getOriginalLanguage().name());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, TestData.BOOK_DTO.getDescription());
    }

    @Test
    public void delete_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        bookDAO.delete(1L);

        Mockito.verify(preparedStatement, Mockito.times(2)).setLong(1, 1L);
        Mockito.verify(preparedStatement, Mockito.times(2)).executeUpdate();
        Mockito.verify(connection, Mockito.times(1)).commit();
    }

    @Test
    public void delete_Should_FailWhenBookNotFound() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(0);

        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.delete(1L));

        Mockito.verify(connection, Mockito.times(1)).rollback();
    }

    @Test
    public void bookDao_Should_ThrowDatabaseOperationException() throws Exception {
        Mockito.when(dataSource.getConnection()).thenThrow(new SQLException());

        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.findById(1L));
        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.findAll());
        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.create(new BookDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.update(1L, new BookDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> bookDAO.delete(1L));
    }
}
