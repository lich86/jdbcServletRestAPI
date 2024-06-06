package chervonnaya.service;

import chervonnaya.dao.BookDAO;
import chervonnaya.dto.BookDTO;
import chervonnaya.model.Book;
import chervonnaya.service.mappers.BaseMapper;

public class BookService extends CrudServiceImpl<Book, BookDTO, BookDAO> {
    public BookService(BookDAO repository, Class<Book> genericType, BaseMapper<Book, BookDTO> mapper) {
        super(repository, genericType, mapper);
    }
}
