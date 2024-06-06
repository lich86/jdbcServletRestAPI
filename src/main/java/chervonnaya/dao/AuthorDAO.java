package chervonnaya.dao;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import chervonnaya.util.ConnectionManager;
import chervonnaya.dao.mappers.AuthorDBMapper;
import chervonnaya.dao.mappers.BookDBMapper;
import chervonnaya.dao.mappers.CopyDBMapper;

import java.sql.*;
import java.util.*;

public class AuthorDAO implements BaseDAO<Author, AuthorDTO> {
    private static final String FIND_BY_ID_SQL = "SELECT * FROM author WHERE author_id = ?";
    private static final String FIND_BOOKS_BY_AUTHOR_ID_SQL = "SELECT * FROM book b JOIN book_author ba ON b.book_id = ba.book_id WHERE author_id = ?";
    private static final String FIND_COPIES_BY_BOOK_ID_SQL = "SELECT * FROM copies WHERE book_id = ?";
    private static final String FIND_ALL_AUTHORS = "SELECT * FROM author";
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO author (first_name, last_name, middle_name, pen_name) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_AUTHOR_SQL = "UPDATE author SET first_name = ?, last_name = ?, middle_name = ?, pen_name = ? WHERE author_id = ?";
    private static final String DELETE_AUTHOR_SQL = "DELETE FROM author WHERE author_id = ?";
    private static final String DELETE_BOOK_SQL = "DELETE FROM book WHERE book_id = ?";
    private static final String DELETE_COPY_BY_BOOK_ID_SQL = "DELETE FROM copies WHERE book_id = ?";
    private static final String DELETE_BOOK_AUTHOR_SQL = "DELETE FROM book_author WHERE author_id = ?";
    private final AuthorDBMapper authorDBMapper = AuthorDBMapper.INSTANCE;
    private final BookDBMapper bookDBMapper = BookDBMapper.INSTANCE;
    private final CopyDBMapper copyDBMapper = CopyDBMapper.INSTANCE;


    @Override
    public Optional<Author> findById(Long authorId) {
        Author author = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(FIND_BY_ID_SQL);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL);
             PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)) {

            authorStatement.setLong(1, authorId);
            ResultSet authorResultSet = authorStatement.executeQuery();
            if (authorResultSet.next()) {
                author = mapToAuthor(authorResultSet, booksStatement, copiesStatement);
            }
        } catch (SQLException e) {
            e.printStackTrace(); //TODO exceptions
        }
        return Optional.ofNullable(author);
    }


    @Override
    public Set<Author> findAll(){
        Set<Author> authorSet = new HashSet<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement allAuthorsStatement = connection.prepareStatement(FIND_ALL_AUTHORS);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL);
             PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)) {
            ResultSet authorResultSet = allAuthorsStatement.executeQuery();
            while (authorResultSet.next()) {
                authorSet.add(mapToAuthor(authorResultSet, booksStatement, copiesStatement));
            }
        } catch (SQLException e) {
            e.printStackTrace(); //TODO exceptions
        }
        return authorSet;

    }

    public void create(AuthorDTO dto) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(INSERT_AUTHOR_SQL)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());
            authorStatement.setString(3, Optional.of(dto.getMiddleName()).orElse(null));
            authorStatement.setString(4, Optional.of(dto.getPenName()).orElse(null));

            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating author failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Creating author failed");
        }
    }

    public void update(Long authorId, AuthorDTO dto) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(UPDATE_AUTHOR_SQL)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());
            authorStatement.setString(3, Optional.of(dto.getMiddleName()).orElse(null));
            authorStatement.setString(4, Optional.of(dto.getPenName()).orElse(null));
            authorStatement.setLong(5, authorId);

            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating author failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Updating author failed");
        }

    }

    public void delete(Long authorId) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection()){
            connection.setAutoCommit(false);
            try(PreparedStatement authorDeleteStatement = connection.prepareStatement(DELETE_AUTHOR_SQL);
                PreparedStatement booksFindStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL);
                PreparedStatement bookDeleteStatement = connection.prepareStatement(DELETE_BOOK_SQL);
                PreparedStatement copyDeleteStatement = connection.prepareStatement(DELETE_COPY_BY_BOOK_ID_SQL);
                PreparedStatement manyToManyDeleteStatement = connection.prepareStatement(DELETE_BOOK_AUTHOR_SQL)) {
                booksFindStatement.setLong(1, authorId);
                ResultSet booksToDelete = booksFindStatement.executeQuery();
                while (booksToDelete.next()) {
                    Long bookId = booksToDelete.getLong("book_id");
                    copyDeleteStatement.setLong(1, bookId);
                    copyDeleteStatement.addBatch();
                    bookDeleteStatement.setLong(1, bookId);
                    bookDeleteStatement.addBatch();
                }
                copyDeleteStatement.executeBatch();
                bookDeleteStatement.executeBatch();
                manyToManyDeleteStatement.setLong(1, authorId);
                manyToManyDeleteStatement.executeUpdate();
                authorDeleteStatement.setLong(1, authorId);
                int affectedRows = authorDeleteStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No author found with id " + authorId);
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not delete author with id: " + authorId);
        }

    }

    private Author mapToAuthor(ResultSet authorResultSet, PreparedStatement booksStatement, PreparedStatement copiesStatement) throws SQLException {
        Author author = authorDBMapper.map(authorResultSet);

        if (author != null) {
            booksStatement.setLong(1, author.getAuthorId());
            ResultSet booksResultSet = booksStatement.executeQuery();
            Set<Book> bookSet = new HashSet<>();
            while (booksResultSet.next()) {
                Book book = bookDBMapper.map(booksResultSet);
                copiesStatement.setLong(1, book.getBookId());
                ResultSet copiesResultSet = copiesStatement.executeQuery();
                Set<Copy> copySet = new HashSet<>();
                while (copiesResultSet.next()) {
                    Copy copy = copyDBMapper.map(copiesResultSet);
                    copy.setBook(book);
                    copySet.add(copy);
                }
                if(!copySet.isEmpty()) {
                    book.setCopies(copySet);
                }
                bookSet.add(book);
            }
            if(!bookSet.isEmpty()) {
                author.setBooks(bookSet);
            }
        }

        return author;
    }

}