package chervonnaya.servlet;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.BookDAO;
import chervonnaya.dto.BookDTO;
import chervonnaya.model.Book;
import chervonnaya.service.mappers.BaseMapper;
import chervonnaya.service.mappers.BookMapper;
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
class BookServletTest extends BaseIntegrationTest {
    private static BookDAO bookDAO = new BookDAO();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static BaseMapper<Book, BookDTO> toDTOMapper = Mappers.getMapper(BookMapper.class);

    @BeforeAll
    static void init(){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    void getBookById_Should_ReturnCorrectBookDTO() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = bookDAO.create(TestData.BOOK_DTO);

            HttpGet request = new HttpGet(TestData.BOOK_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(rootNode.get("originalTitle").asText(), TestData.BOOK_DTO.getOriginalTitle());
                Assertions.assertEquals(rootNode.get("originalLanguage").asText(), TestData.BOOK_DTO.getOriginalLanguage().toString());
                Assertions.assertEquals(rootNode.get("description").asText(), TestData.BOOK_DTO.getDescription());
                return response;});
        }
    }

    @Test
    void getAllBooks_Should_ReturnCorrectAmountOfBooks() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Set<Book> bookSet = bookDAO.findAll();
            Long booksCount = (long) bookSet.size();
            HttpGet request = new HttpGet(TestData.BOOK_URL);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(booksCount, rootNode.size());
                return response;
            });

        }
    }

    @Test
    void postBook_Should_ReturnIsCreatedStatus() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.BOOK_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.BOOK_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_CREATED);
                return response;
            });
        }
    }

    @Test
    void postBook_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.BOOK_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.WRONG_BOOK_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return response;
            });
        }
    }

    @Test
    void putBook_Should_UpdateAsExpected() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Book book = bookDAO.findById(1L).orElse(null);
            book.setDescription("");
            Assertions.assertNotEquals(book.getDescription(), TestData.BOOK_DESCRIPTION);
            BookDTO bookDTO = toDTOMapper.map(book);
            bookDTO.setDescription(TestData.BOOK_DESCRIPTION);

            HttpPut request = new HttpPut(TestData.BOOK_URL + "/1");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(bookDTO), ContentType.create("application/json", "UTF-8")));
            httpClient.execute(request, response -> response);
            book = bookDAO.findById(1L).orElse(null);

            Assertions.assertEquals(TestData.BOOK_DESCRIPTION, book.getDescription());
        }
    }

    @Test
    void deleteBook_Should_DeleteBookFromDB() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = bookDAO.create(TestData.BOOK_DTO);

            HttpDelete request = new HttpDelete(TestData.BOOK_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, response.getCode());
                return response;
            });
        }
    }



}