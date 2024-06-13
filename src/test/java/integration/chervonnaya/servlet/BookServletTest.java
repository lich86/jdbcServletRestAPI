package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.BookDAO;
import chervonnaya.dto.BookDTO;
import chervonnaya.model.Book;
import chervonnaya.service.BookServiceImpl;
import chervonnaya.service.CrudService;
import chervonnaya.service.mappers.BookMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.*;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


@Testcontainers
public class BookServletTest extends BaseIntegrationTest {
    private final BookDAO bookDAO = new BookDAO(dataSource);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CrudService<BookDTO> bookService = new BookServiceImpl(bookDAO, Book.class, Mappers.getMapper(BookMapper.class));
    private final BookServlet bookServlet = new BookServlet();
    private final StringWriter stringWriter = new StringWriter();
    @Mock
    private HttpServletRequest request;
    @Spy
    private HttpServletResponse response;
    private AutoCloseable closeable;

    @BeforeEach
    void initServlet() throws NoSuchFieldException, IllegalAccessException, IOException {
        closeable = MockitoAnnotations.openMocks(this);
        injectFields();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(printWriter);
    }

    private void injectFields() throws NoSuchFieldException, IllegalAccessException {
        Field bookServiceField = BookServlet.class.getDeclaredField("bookService");
        Field objectMapperField = BookServlet.class.getDeclaredField("objectMapper");
        Field dataSourceField = BookServlet.class.getDeclaredField("dataSource");
        bookServiceField.setAccessible(true);
        objectMapperField.setAccessible(true);
        dataSourceField.setAccessible(true);
        bookServiceField.set(bookServlet, bookService);
        objectMapperField.set(bookServlet, objectMapper);
        dataSourceField.set(bookServlet, dataSource);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    void getBookById_Should_ReturnCorrectBookDTO() throws IOException {
        Long id = bookDAO.create(TestData.BOOK_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);
        BookDTO expectedDTO = bookService.getById(id).orElse(null);
        String expectedResponse = objectMapper.writeValueAsString(expectedDTO);

        bookServlet.doGet(request, response);

        Assertions.assertEquals(expectedResponse, stringWriter.toString());
    }

    @Test
    void getAllBooks_Should_ReturnCorrectAmountOfBooks() throws IOException {
        Set<Book> bookSet = bookDAO.findAll();
        Long booksCount = (long) bookSet.size();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        bookServlet.doGet(request, response);

        Set<BookDTO> bookDtoSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        Assertions.assertEquals(booksCount, bookDtoSet.size());
    }

    @Test
    void getAllBooks_Should_ReturnCorrectSetOfBooks() throws IOException {
        Set<BookDTO> bookDtoSet = bookService.getAll();
        Comparator<BookDTO> comparator = Comparator.comparingLong(BookDTO::getId);
        List<BookDTO> expectedSortedDTOList = bookDtoSet.stream().sorted(comparator).toList();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        bookServlet.doGet(request, response);
        Set<BookDTO> actualDTOSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        List<BookDTO> actualSortedDTOList = actualDTOSet.stream().sorted(comparator).toList();

        Assertions.assertIterableEquals(expectedSortedDTOList, actualSortedDTOList);

    }

    @Test
    void postBook_Should_ReturnCreatedString() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.BOOK_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        bookServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains("Book created with ID: "));
    }

    @Test
    void postBook_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.WRONG_BOOK_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        bookServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().isEmpty());
    }

    @Test
    void putBook_Should_UpdateAsExpected() throws IOException {
        BookDTO bookDTO = bookService.getById(1L).orElse(null);
        bookDTO.setDescription("");
        bookDAO.update(1L, bookDTO);
        bookDTO.setDescription(TestData.BOOK_DESCRIPTION);
        String requestString = objectMapper.writeValueAsString(bookDTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));
        Mockito.when(request.getPathInfo()).thenReturn("/1");

        bookServlet.doPut(request, response);

        BookDTO actualBookDTO = objectMapper.readValue(stringWriter.toString(), BookDTO.class);
        Assertions.assertEquals(bookDTO, actualBookDTO);
    }

    @Test
    void deleteBook_Should_DeleteEntityFromDB() {
        Long id = bookDAO.create(TestData.BOOK_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);

        bookServlet.doDelete(request, response);

        Assertions.assertTrue(bookDAO.findById(id).isEmpty());
    }

}
