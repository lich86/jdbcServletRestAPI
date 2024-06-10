package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class AuthorDAOTest extends BaseIntegrationTest {

    AuthorDAO authorDAO = new AuthorDAO();

    @Test
    public void testFindById() {
        Long authorId = authorDAO.create(TestData.AUTHOR_DTO);

        Optional<Author> optionalAuthor = authorDAO.findById(authorId);
        assertTrue(optionalAuthor.isPresent());

        Author author = optionalAuthor.get();
        assertEquals(TestData.AUTHOR_DTO.getFirstName(), author.getFirstName());
        assertEquals(TestData.AUTHOR_DTO.getLastName(), author.getLastName());
        assertEquals(TestData.AUTHOR_DTO.getMiddleName(), author.getMiddleName());
        assertEquals(TestData.AUTHOR_DTO.getPenName(), author.getPenName());
    }
/*
    @Test
    public void testFindAll() {
        AuthorDTO authorDTO1 = new AuthorDTO("John", "Doe", null, null);
        AuthorDTO authorDTO2 = new AuthorDTO("Jane", "Smith", null, null);

        authorDAO.create(authorDTO1);
        authorDAO.create(authorDTO2);

        Set<Author> authors = authorDAO.findAll();
        assertEquals(2, authors.size());
    }

    @Test
    public void testUpdate() {
        AuthorDTO authorDTO = new AuthorDTO("John", "Doe", null, null);
        Long authorId = authorDAO.create(authorDTO);

        AuthorDTO updatedAuthorDTO = new AuthorDTO("Jane", "Smith", null, null);
        authorDAO.update(authorId, updatedAuthorDTO);

        Optional<Author> optionalAuthor = authorDAO.findById(authorId);
        assertTrue(optionalAuthor.isPresent());

        Author author = optionalAuthor.get();
        assertEquals("Jane", author.getFirstName());
        assertEquals("Smith", author.getLastName());
    }

    @Test
    public void testDelete() {
        AuthorDTO authorDTO = new AuthorDTO("John", "Doe", null, null);
        Long authorId = authorDAO.create(authorDTO);

        authorDAO.delete(authorId);

        Optional<Author> optionalAuthor = authorDAO.findById(authorId);
        assertFalse(optionalAuthor.isPresent());
    }
} */
}