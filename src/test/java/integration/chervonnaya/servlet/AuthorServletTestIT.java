package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.AuthorDAO;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.service.AuthorServiceImpl;
import chervonnaya.service.CrudService;
import chervonnaya.service.mappers.AuthorMapper;
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
public class AuthorServletTestIT extends BaseIntegrationTest {
    private final AuthorDAO authorDAO = new AuthorDAO(dataSource);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CrudService<AuthorDTO> authorService = new AuthorServiceImpl(authorDAO, Author.class, Mappers.getMapper(AuthorMapper.class));
    private final AuthorServlet authorServlet = new AuthorServlet();
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
        Field authorServiceField = AuthorServlet.class.getDeclaredField("authorService");
        Field objectMapperField = AuthorServlet.class.getDeclaredField("objectMapper");
        Field dataSourceField = AuthorServlet.class.getDeclaredField("dataSource");
        authorServiceField.setAccessible(true);
        objectMapperField.setAccessible(true);
        dataSourceField.setAccessible(true);
        authorServiceField.set(authorServlet, authorService);
        objectMapperField.set(authorServlet, objectMapper);
        dataSourceField.set(authorServlet, dataSource);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    void getAuthorById_Should_ReturnCorrectAuthorDTO() throws IOException {
        Long id = authorDAO.create(TestData.AUTHOR_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);
        AuthorDTO expectedDTO = authorService.getById(id).orElse(null);
        String expectedResponse = objectMapper.writeValueAsString(expectedDTO);

        authorServlet.doGet(request, response);

        Assertions.assertEquals(expectedResponse, stringWriter.toString());
    }

    @Test
    void getAllAuthors_Should_ReturnCorrectAmountOfAuthors() throws IOException {
        Set<Author> authorSet = authorDAO.findAll();
        Long authorsCount = (long) authorSet.size();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        authorServlet.doGet(request, response);

        Set<AuthorDTO> authorDtoSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        Assertions.assertEquals(authorsCount, authorDtoSet.size());
    }

    @Test
    void getAllAuthors_Should_ReturnCorrectSetOfAuthors() throws IOException {
        Set<AuthorDTO> authorDtoSet = authorService.getAll();
        Comparator<AuthorDTO> comparator = Comparator.comparingLong(AuthorDTO::getId);
        List<AuthorDTO> expectedSortedDTOList = authorDtoSet.stream().sorted(comparator).toList();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        authorServlet.doGet(request, response);
        Set<AuthorDTO> actualDTOSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        List<AuthorDTO> actualSortedDTOList = actualDTOSet.stream().sorted(comparator).toList();

        Assertions.assertIterableEquals(expectedSortedDTOList, actualSortedDTOList);

    }

    @Test
    void postAuthor_Should_ReturnCreatedString() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.AUTHOR_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        authorServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains("Author created with ID: "));
    }

    @Test
    void postAuthor_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.WRONG_AUTHOR_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        authorServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().isEmpty());
    }

    @Test
    void putAuthor_Should_UpdateAsExpected() throws IOException {
        AuthorDTO authorDTO = authorService.getById(1L).orElse(null);
        authorDTO.setPenName("");
        authorDAO.update(1L, authorDTO);
        authorDTO.setPenName(TestData.AUTHOR_PEN_NAME);
        String requestString = objectMapper.writeValueAsString(authorDTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));
        Mockito.when(request.getPathInfo()).thenReturn("/1");

        authorServlet.doPut(request, response);

        AuthorDTO actualAuthorDTO = objectMapper.readValue(stringWriter.toString(), AuthorDTO.class);
        Assertions.assertEquals(authorDTO, actualAuthorDTO);
    }

    @Test
    void deleteAuthor_Should_DeleteEntityFromDB() {
        Long id = authorDAO.create(TestData.AUTHOR_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);

        authorServlet.doDelete(request, response);

        Assertions.assertTrue(authorDAO.findById(id).isEmpty());
    }

}
