package chervonnaya.dao;

import chervonnaya.TestData;
import chervonnaya.dao.mappers.BookDBMapper;
import chervonnaya.dto.CopyDTO;
import chervonnaya.exception.DatabaseOperationException;
import chervonnaya.model.Copy;
import chervonnaya.dao.mappers.CopyDBMapper;
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

public class CopyDAOTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private CopyDBMapper copyDBMapper;
    @Mock
    private BookDBMapper bookDBMapper;
    private CopyDAO copyDAO;
    private AutoCloseable closeable;


    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        copyDAO = new CopyDAO(dataSource);
        FieldUtils.writeField(copyDAO, "copyDBMapper", copyDBMapper, true);
        FieldUtils.writeField(copyDAO, "bookDBMapper", bookDBMapper, true);

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
        Mockito.when(copyDBMapper.map(resultSet)).thenReturn(TestData.COPY1);
        Mockito.when(bookDBMapper.map(resultSet)).thenReturn(TestData.BOOK1);


        Optional<Copy> actualCopy = copyDAO.findById(1L);

        Assertions.assertTrue(actualCopy.isPresent());
        Assertions.assertEquals(TestData.COPY1, actualCopy.get());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findById_Should_Fail() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);

        Optional<Copy> result = copyDAO.findById(1L);

        Assertions.assertFalse(result.isPresent());
        Mockito.verify(preparedStatement, Mockito.atLeast(1)).setLong(1, 1L);
    }

    @Test
    public void findAll_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next())
                .thenReturn(true).thenReturn(true)
                .thenReturn(true).thenReturn(true)
                .thenReturn(false);
        Mockito.when(copyDBMapper.map(resultSet)).thenReturn(TestData.COPY1).thenReturn(TestData.COPY2);

        Set<Copy> copys = copyDAO.findAll();

        Assertions.assertEquals(2, copys.size());
        Assertions.assertTrue(copys.contains(TestData.COPY1));
        Assertions.assertTrue(copys.contains(TestData.COPY2));
    }

    @Test
    public void create_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString(), Mockito.eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);
        Mockito.when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getLong(1)).thenReturn(1L);

        Long copyId = copyDAO.create(TestData.COPY_DTO);

        Assertions.assertEquals(1L, copyId);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.COPY_DTO.getTitle());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.COPY_DTO.getLanguage().name());
        Mockito.verify(preparedStatement, Mockito.times(1)).setDouble(3, TestData.COPY_DTO.getPrice());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(4, TestData.COPY_DTO.getPublishingHouse());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(5, TestData.COPY_DTO.getPublishingYear().toString());
        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(7, TestData.COPY_DTO.getBookId());

    }

    @Test
    public void update_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        copyDAO.update(1L, TestData.COPY_DTO);

        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(8, 1L);
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, TestData.COPY_DTO.getTitle());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, TestData.COPY_DTO.getLanguage().name());
        Mockito.verify(preparedStatement, Mockito.times(1)).setDouble(3, TestData.COPY_DTO.getPrice());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(4, TestData.COPY_DTO.getPublishingHouse());
        Mockito.verify(preparedStatement, Mockito.times(1)).setString(5, TestData.COPY_DTO.getPublishingYear().toString());
        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(7, TestData.COPY_DTO.getBookId());

    }

    @Test
    public void delete_Should_Succeed() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> copyDAO.delete(1L));

        Mockito.verify(preparedStatement, Mockito.times(1)).setLong(1, 1L);
        Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();

    }

    @Test
    public void delete_Should_FailWhenCopyNotFound() throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(false);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(0);

        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.delete(1L));
    }

    @Test
    public void copyDao_Should_ThrowDatabaseOperationException() throws Exception {
        Mockito.when(dataSource.getConnection()).thenThrow(new SQLException());

        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.findById(1L));
        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.findAll());
        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.create(new CopyDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.update(1L, new CopyDTO()));
        Assertions.assertThrows(DatabaseOperationException.class, () -> copyDAO.delete(1L));
    }
}
