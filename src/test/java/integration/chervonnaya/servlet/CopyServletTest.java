package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.CopyServiceImpl;
import chervonnaya.service.CrudService;
import chervonnaya.service.mappers.CopyMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class CopyServletTest extends BaseIntegrationTest {
    private final CopyDAO copyDAO = new CopyDAO(dataSource);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CrudService<CopyDTO> copyService = new CopyServiceImpl(copyDAO, Copy.class, Mappers.getMapper(CopyMapper.class));
    private final CopyServlet copyServlet = new CopyServlet();
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
        objectMapper.registerModule(new JavaTimeModule());
    }

    private void injectFields() throws NoSuchFieldException, IllegalAccessException {
        Field copyServiceField = CopyServlet.class.getDeclaredField("copyService");
        Field objectMapperField = CopyServlet.class.getDeclaredField("objectMapper");
        Field dataSourceField = CopyServlet.class.getDeclaredField("dataSource");
        copyServiceField.setAccessible(true);
        objectMapperField.setAccessible(true);
        dataSourceField.setAccessible(true);
        copyServiceField.set(copyServlet, copyService);
        objectMapperField.set(copyServlet, objectMapper);
        dataSourceField.set(copyServlet, dataSource);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    void getCopyById_Should_ReturnCorrectCopyDTO() throws IOException {
        Long id = copyDAO.create(TestData.COPY_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);
        CopyDTO expectedDTO = copyService.getById(id).orElse(null);
        String expectedResponse = objectMapper.writeValueAsString(expectedDTO);

        copyServlet.doGet(request, response);

        Assertions.assertEquals(expectedResponse, stringWriter.toString());
    }

    @Test
    void getAllCopys_Should_ReturnCorrectAmountOfCopys() throws IOException {
        Set<Copy> copySet = copyDAO.findAll();
        Long copysCount = (long) copySet.size();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        copyServlet.doGet(request, response);

        Set<CopyDTO> copyDtoSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        Assertions.assertEquals(copysCount, copyDtoSet.size());
    }

    @Test
    void getAllCopys_Should_ReturnCorrectSetOfCopys() throws IOException {
        Set<CopyDTO> copyDtoSet = copyService.getAll();
        Comparator<CopyDTO> comparator = Comparator.comparingLong(CopyDTO::getId);
        List<CopyDTO> expectedSortedDTOList = copyDtoSet.stream().sorted(comparator).toList();
        Mockito.when(request.getPathInfo()).thenReturn("/");

        copyServlet.doGet(request, response);
        Set<CopyDTO> actualDTOSet = objectMapper.readValue(stringWriter.toString(), new TypeReference<>() {});
        List<CopyDTO> actualSortedDTOList = actualDTOSet.stream().sorted(comparator).toList();

        Assertions.assertIterableEquals(expectedSortedDTOList, actualSortedDTOList);

    }

    @Test
    void postCopy_Should_ReturnCreatedString() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.COPY_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        copyServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains("Copy created with ID: "));
    }

    @Test
    void postCopy_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        String requestString = objectMapper.writeValueAsString(TestData.WRONG_COPY_DTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));

        copyServlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().isEmpty());
    }

    @Test
    void putCopy_Should_UpdateAsExpected() throws IOException {
        CopyDTO copyDTO = copyService.getById(1L).orElse(null);
        copyDTO.setPublishingHouse("");
        copyDAO.update(1L, copyDTO);
        copyDTO.setPublishingHouse(TestData.COPY_PUBLISHING_HOUSE);
        String requestString = objectMapper.writeValueAsString(copyDTO);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestString)));
        Mockito.when(request.getPathInfo()).thenReturn("/1");

        copyServlet.doPut(request, response);

        CopyDTO actualCopyDTO = objectMapper.readValue(stringWriter.toString(), CopyDTO.class);
        Assertions.assertEquals(copyDTO, actualCopyDTO);
    }

    @Test
    void deleteCopy_Should_DeleteEntityFromDB() {
        Long id = copyDAO.create(TestData.COPY_DTO);
        Mockito.when(request.getPathInfo()).thenReturn("/" + id);

        copyServlet.doDelete(request, response);

        Assertions.assertTrue(copyDAO.findById(id).isEmpty());
    }

}
