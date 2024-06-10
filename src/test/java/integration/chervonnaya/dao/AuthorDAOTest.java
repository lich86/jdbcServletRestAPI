package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.model.Author;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class AuthorDAOTest extends BaseIntegrationTest {
    AuthorDAO authorDAO = new AuthorDAO(dataSource);

    @Test
    void create() {
        Long id = authorDAO.create(TestData.AUTHOR_DTO);
        assertTrue(id > 0);

        Optional<Author> optionalAuthor = authorDAO.findById(id);
        assertTrue(optionalAuthor.isPresent());
    }
}