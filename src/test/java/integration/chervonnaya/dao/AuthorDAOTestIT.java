package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestDataIT;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.service.mappers.AuthorMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class AuthorDAOTestIT extends BaseIntegrationTest {
    private final AuthorDAO authorDAO = new AuthorDAO(dataSource);
    private final BookDAO bookDAO = new BookDAO(dataSource);
    private final AuthorMapper toDTOMapper = AuthorMapper.INSTANCE;

    @Test
    void findAuthorById_Should_ReturnCorrectAuthor() {
        AuthorDTO expextedAuthorDTO = TestDataIT.AUTHOR_DTO;
        Long id = authorDAO.create(expextedAuthorDTO);
        expextedAuthorDTO.setId(id);

        Author author = authorDAO.findById(id).orElse(null);

        AuthorDTO actualAuthorDTO = toDTOMapper.map(author);
        Assertions.assertEquals(expextedAuthorDTO, actualAuthorDTO);
    }

    @Test
    void findAuthors_Should_ReturnAuthorSet() {
        Set<Author> authors = authorDAO.findAll();

        assertNotNull(authors);
    }

    @Test
    void createAuthor_Should_CreateEntityInDB() {
        Long id = authorDAO.create(TestDataIT.AUTHOR_DTO);

        assertTrue(id > 0);
        Optional<Author> optionalAuthor = authorDAO.findById(id);
        assertTrue(optionalAuthor.isPresent());
    }

    @Test
    void updateAuthor_Should_UpdateEntityAsExpected() {
        Author author = authorDAO.findById(1L).orElse(null);

        Assertions.assertNotEquals(author.getPenName(), TestDataIT.AUTHOR_PEN_NAME);

        AuthorDTO authorDTO = toDTOMapper.map(author);
        authorDTO.setPenName(TestDataIT.AUTHOR_PEN_NAME);
        authorDAO.update(1L, authorDTO);
        author = authorDAO.findById(1L).orElse(null);

        Assertions.assertEquals(TestDataIT.AUTHOR_PEN_NAME, author.getPenName());

    }

    @Test
    void deleteAuthor_Should_DeleteAuthorFromDB() {
        authorDAO.delete(1L);

        Assertions.assertTrue(authorDAO.findById(1L).isEmpty());
    }

    @Test
    void deleteAuthor_Should_AlsoDeleteAuthorsBooks() {
        Author author = authorDAO.findById(2L).orElse(null);
        List<Book> bookList = new ArrayList<>(author.getBooks());
        Assertions.assertNotNull(bookList);

        authorDAO.delete(2L);

        for (Book book : bookList) {
            Assertions.assertTrue(bookDAO.findById(book.getId()).isEmpty());
        }

    }

    @Test
    void findById_Should_ReturnEmptyOptionalIfWrongId() {
        Assertions.assertTrue(authorDAO.findById(99L).isEmpty());
    }




}