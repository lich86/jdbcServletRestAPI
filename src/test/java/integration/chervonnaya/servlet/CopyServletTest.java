package chervonnaya.servlet;


import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.model.enums.Language;
import chervonnaya.service.mappers.CopyMapper;
import chervonnaya.service.mappers.BaseMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.Year;
import java.util.Set;

@Testcontainers
public class CopyServletTest extends BaseIntegrationTest {
    private static CopyDAO copyDAO = new CopyDAO();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static BaseMapper<Copy, CopyDTO> toDTOMapper = Mappers.getMapper(CopyMapper.class);

    @BeforeAll
    static void init(){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void getCopyById_Should_ReturnCorrectCopyDTO() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = copyDAO.create(TestData.COPY_DTO);

            HttpGet request = new HttpGet(TestData.COPY_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(rootNode.get("title").asText(), TestData.COPY_DTO.getTitle());
                Assertions.assertEquals(Language.valueOf(rootNode.get("language").asText()), TestData.COPY_DTO.getLanguage());
                Assertions.assertEquals(Double.parseDouble(rootNode.get("price").asText()), TestData.COPY_DTO.getPrice());
                Assertions.assertEquals(rootNode.get("publishingHouse").asText(), TestData.COPY_DTO.getPublishingHouse());
                Assertions.assertEquals(Year.parse(rootNode.get("publishingYear").asText()), TestData.COPY_DTO.getPublishingYear());
                Assertions.assertEquals(Long.parseLong(rootNode.get("book_id").asText()), TestData.COPY_DTO.getBookId());
                return response;});
        }
    }

    @Test
    void getAllCopies_Should_ReturnCorrectAmountOfCopys() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Set<Copy> copySet = copyDAO.findAll();
            Long copysCount = (long) copySet.size();
            HttpGet request = new HttpGet(TestData.COPY_URL);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_OK);
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                Assertions.assertEquals(copysCount, rootNode.size());
                return response;
            });

        }
    }

    @Test
    void postCopy_Should_ReturnIsCreatedStatus() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.COPY_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.COPY_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_CREATED);
                return response;
            });
        }
    }

    @Test
    void postCopy_ShouldNot_CreateEntityWithWrongDTO() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TestData.COPY_URL);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(TestData.WRONG_COPY_DTO)));
            httpClient.execute(request, response -> {
                Assertions.assertEquals(response.getCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return response;
            });
        }
    }

    @Test
    void putCopy_Should_UpdateAsExpected() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Copy copy = copyDAO.findById(1L).orElse(null);
            copy.setPublishingHouse("");
            Assertions.assertNotEquals(copy.getPublishingHouse(), TestData.COPY_PUBLISHING_HOUSE);
            CopyDTO copyDTO = toDTOMapper.map(copy);
            copyDTO.setPublishingHouse(TestData.COPY_PUBLISHING_HOUSE);

            HttpPut request = new HttpPut(TestData.COPY_URL + "/1");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(copyDTO), ContentType.create("application/json", "UTF-8")));
            httpClient.execute(request, response -> response);
            copy = copyDAO.findById(1L).orElse(null);

            Assertions.assertEquals(TestData.COPY_PUBLISHING_HOUSE, copy.getPublishingHouse());
        }
    }

    @Test
    void deleteCopy_Should_DeleteCopyFromDB() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Long id = copyDAO.create(TestData.COPY_DTO);

            HttpDelete request = new HttpDelete(TestData.COPY_URL + "/" + id);
            httpClient.execute(request, response -> {
                Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, response.getCode());
                return response;
            });
        }
    }


}
