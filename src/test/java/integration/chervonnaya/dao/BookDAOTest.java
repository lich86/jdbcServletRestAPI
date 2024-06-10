package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.model.Book;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class BookDAOTest extends BaseIntegrationTest {
    BookDAO bookDAO = new BookDAO(dataSource);

    @Test
    void create() {
        Long id = bookDAO.create(TestData.BOOK_DTO);
        assertTrue(id > 0);

        Optional<Book> optionalAuthor = bookDAO.findById(id);
        assertTrue(optionalAuthor.isPresent());
    }

}