package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dto.BookDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.service.mappers.BookMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class BookDAOTestIT extends BaseIntegrationTest {
    private final BookDAO bookDAO = new BookDAO(dataSource);
    private final BookMapper toDTOMapper = BookMapper.INSTANCE;

    @Test
    void findBookById_Should_ReturnCorrectBook() {
        BookDTO expextedBookDTO = TestData.BOOK_DTO;
        Long id = bookDAO.create(expextedBookDTO);
        expextedBookDTO.setId(id);

        Book book = bookDAO.findById(id).orElse(null);

        BookDTO actualBookDTO = toDTOMapper.map(book);
        Assertions.assertEquals(expextedBookDTO, actualBookDTO);
    }

    @Test
    void findBooks_Should_ReturnBookSet() {
        Set<Book> books = bookDAO.findAll();

        assertNotNull(books);
    }

    @Test
    void createBook_Should_CreateEntityInDB() {
        Long id = bookDAO.create(TestData.BOOK_DTO);

        assertTrue(id > 0);
        Optional<Book> optionalBook = bookDAO.findById(id);
        assertTrue(optionalBook.isPresent());
    }

    @Test
    void updateBook_Should_UpdateEntityAsExpected() {
        Book book = bookDAO.findById(1L).orElse(null);

        Assertions.assertNotEquals(book.getDescription(), TestData.BOOK_DESCRIPTION);

        BookDTO bookDTO = toDTOMapper.map(book);
        bookDTO.setDescription(TestData.BOOK_DESCRIPTION);
        bookDAO.update(1L, bookDTO);
        book = bookDAO.findById(1L).orElse(null);

        Assertions.assertEquals(TestData.BOOK_DESCRIPTION, book.getDescription());

    }

    @Test
    void deleteBook_Should_DeleteAuthorFromDB() {
        bookDAO.delete(1L);

        Assertions.assertTrue(bookDAO.findById(1L).isEmpty());
    }

    @Test
    void deleteBook_ShouldNot_DeleteBookAuthors() {
        Book book = bookDAO.findById(2L).orElse(null);
        List<Author> authorList = new ArrayList<>(book.getAuthors());

        bookDAO.delete(2L);


    }

    @Test
    void findById_Should_ReturnEmptyOptionalIfWrongId() {
        Assertions.assertTrue(bookDAO.findById(99L).isEmpty());
    }
}