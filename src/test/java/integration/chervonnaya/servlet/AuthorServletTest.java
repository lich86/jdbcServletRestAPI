package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.dao.AuthorDAO;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.service.AuthorServiceImpl;
import chervonnaya.service.CrudService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Testcontainers
public class AuthorServletTest extends BaseIntegrationTest {
    private AuthorDAO authorDAO = new AuthorDAO();

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void getAuthorById_Should_ReturnCorrectAuthorDTO() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = authorDAO.create(ServletTestData.AUTHOR_DTO);

            HttpGet request = new HttpGet(ServletTestData.AUTHOR_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(rootNode.get("firstName").asText(), ServletTestData.AUTHOR_DTO.getFirstName());
                Assertions.assertEquals(rootNode.get("lastName").asText(), ServletTestData.AUTHOR_DTO.getLastName());
                Assertions.assertEquals(rootNode.get("middleName").asText(), ServletTestData.AUTHOR_DTO.getMiddleName());
                Assertions.assertEquals(rootNode.get("penName").asText(), ServletTestData.AUTHOR_DTO.getPenName());
                return response;});
        }
    }


}
