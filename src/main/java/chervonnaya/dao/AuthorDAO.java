package chervonnaya.dao;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import chervonnaya.util.ConnectionManager;
import chervonnaya.util.mappers.AuthorMapper;
import chervonnaya.util.mappers.BookMapper;
import chervonnaya.util.mappers.CopyMapper;

import java.sql.*;
import java.util.*;

public class AuthorDAO {
    private static final String FIND_BY_ID_SQL = "SELECT * FROM author WHERE author_id = ?";
    private static final String FIND_BOOKS_BY_AUTHOR_ID_SQL = "SELECT * FROM book WHERE author_id = ?";
    private static final String FIND_COPIES_BY_BOOK_ID_SQL = "SELECT * FROM copy WHERE book_id = ?";
    private static final String FIND_ALL_AUTHORS = "SELECT * FROM author";
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO author (first_name, last_name, middle_name, pen_name) VALUES (?, ?, ?, ?)";
    private static final String INSERT_BOOK_SQL = "INSERT INTO book (book_title) VALUES (?)";
    private static final String UPDATE_AUTHOR_SQL = "UPDATE author SET first_name = ?, last_name = ?, middle_name = ?, pen_name = ? WHERE author_id = ?";
    private final AuthorMapper authorMapper = AuthorMapper.INSTANCE;
    private final BookMapper bookMapper = BookMapper.INSTANCE;
    private final CopyMapper copyMapper = CopyMapper.INSTANCE;

    public Optional<Author> findById(Long id) {
        Author author = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(FIND_BY_ID_SQL);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL);
             PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)) {

            authorStatement.setLong(1, id);
            ResultSet authorResultSet = authorStatement.executeQuery();
            if (authorResultSet.next()) {
                author = mapToAuthor(authorResultSet, booksStatement, copiesStatement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(author);
    }


    public List<Author> findAll() throws SQLException{
        List<Author> authorList = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement allAuthorsStatement = connection.prepareStatement(FIND_ALL_AUTHORS);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL);
             PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)) {
            ResultSet authorResultSet = allAuthorsStatement.executeQuery();
            while (authorResultSet.next()) {
                authorList.add(mapToAuthor(authorResultSet, booksStatement, copiesStatement));
            }
        }
        return authorList;

    }

    public void create(AuthorDTO dto) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(INSERT_AUTHOR_SQL);
             PreparedStatement bookStatement = connection.prepareStatement(INSERT_BOOK_SQL)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());

            if (dto.getMiddleName() != null) {
                authorStatement.setString(3, dto.getMiddleName());
            } else {
                authorStatement.setNull(3, Types.VARCHAR);
            }

            if (dto.getPenName() != null) {
                authorStatement.setString(4, dto.getPenName());
            } else {
                authorStatement.setNull(4, Types.VARCHAR);
            }
            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            if(!dto.getBookTitles().isEmpty()) {
                for (String string : dto.getBookTitles()) {
                    bookStatement.setString(1, string);
                    affectedRows = bookStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating books for an author failed, no rows affected.");
                    }
                }
            }
        }
    }

    public void update(Long id, AuthorDTO dto) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(INSERT_AUTHOR_SQL);
             PreparedStatement bookStatement = connection.prepareStatement(INSERT_BOOK_SQL)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());

            if (dto.getMiddleName() != null) {
                authorStatement.setString(3, dto.getMiddleName());
            } else {
                authorStatement.setNull(3, Types.VARCHAR);
            }

            if (dto.getPenName() != null) {
                authorStatement.setString(4, dto.getPenName());
            } else {
                authorStatement.setNull(4, Types.VARCHAR);
            }
            authorStatement.setLong(5, id);
            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating order failed, no rows affected.");
            }
        }

    }

    private Author mapToAuthor(ResultSet authorResultSet, PreparedStatement booksStatement, PreparedStatement copiesStatement) throws SQLException {
        Author author = authorMapper.map(authorResultSet);

        if (author != null) {
            booksStatement.setLong(1, author.getAuthorId());
            ResultSet booksResultSet = booksStatement.executeQuery();
            Set<Book> books = new HashSet<>();
            while (booksResultSet.next()) {
                Book book = bookMapper.map(booksResultSet);
                copiesStatement.setLong(1, book.getBookId());
                ResultSet copiesResultSet = copiesStatement.executeQuery();
                Set<Copy> copies = new HashSet<>();
                while (copiesResultSet.next()) {
                    Copy copy = copyMapper.map(copiesResultSet);
                    copy.setBook(book);
                    copies.add(copy);
                }
                book.setCopies(copies);
                books.add(book);
            }
            author.setBooks(books);
        }

        return author;
    }

}