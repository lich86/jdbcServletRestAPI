package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.AuthorDAO;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.service.mappers.AuthorMapper;
import chervonnaya.service.mappers.BaseMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Testcontainers
public class AuthorServletTest extends BaseIntegrationTest {
    private static AuthorDAO authorDAO = new AuthorDAO();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static BaseMapper<Author, AuthorDTO> toDTOMapper = Mappers.getMapper(AuthorMapper.class);

    @BeforeAll
    static void init(){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    @Test
    void getAuthorById_Should_ReturnCorrectAuthorDTO() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = authorDAO.create(TestData.AUTHOR_DTO);
            while (true);

            /*HttpGet request = new HttpGet(TestData.AUTHOR_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(rootNode.get("firstName").asText(), TestData.AUTHOR_DTO.getFirstName());
                Assertions.assertEquals(rootNode.get("lastName").asText(), TestData.AUTHOR_DTO.getLastName());
                Assertions.assertEquals(rootNode.get("middleName").asText(), TestData.AUTHOR_DTO.getMiddleName());
                Assertions.assertEquals(rootNode.get("penName").asText(), TestData.AUTHOR_DTO.getPenName());
                return response;});*/
        }
    }

    @Test
    void getAllAuthors_Should_ReturnCorrectAmountOfAuthors() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Set<Author> authorSet = authorDAO.findAll();
            Long authorsCount = (long) authorSet.size();
            HttpGet request = new HttpGet(TestData.AUTHOR_URL);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(authorsCount, rootNode.size());
                return response;
            });

        }
    }

    @Test
    void postAuthor_Should_ReturnIsCreatedStatus() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.AUTHOR_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.AUTHOR_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_CREATED);
                return response;
            });
        }
    }

    @Test
    void postAuthor_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.AUTHOR_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.WRONG_AUTHOR_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return response;
            });
        }
    }

    @Test
    void putAuthor_Should_UpdateAsExpected() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Author author = authorDAO.findById(1L).orElse(null);
            author.setPenName("");
            Assertions.assertNotEquals(author.getPenName(), TestData.AUTHOR_PEN_NAME);
            AuthorDTO authorDTO = toDTOMapper.map(author);
            authorDTO.setPenName(TestData.AUTHOR_PEN_NAME);

            HttpPut request = new HttpPut(TestData.AUTHOR_URL + "/1");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(authorDTO), ContentType.create("application/json", "UTF-8")));
            httpClient.execute(request, response -> response);
            author = authorDAO.findById(1L).orElse(null);

            Assertions.assertEquals(TestData.AUTHOR_PEN_NAME, author.getPenName());
        }
    }

    @Test
    void deleteAuthor_Should_DeleteAuthorFromDB() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = authorDAO.create(TestData.AUTHOR_DTO);

            HttpDelete request = new HttpDelete(TestData.AUTHOR_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, response.getCode());
                return response;
            });
        }
    }


}
