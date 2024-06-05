package chervonnaya.dao;


import chervonnaya.dto.BookDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import chervonnaya.util.ConnectionManager;
import chervonnaya.dao.mappers.AuthorDBMapper;
import chervonnaya.dao.mappers.BookDBMapper;
import chervonnaya.dao.mappers.CopyDBMapper;

import java.sql.*;
import java.util.*;

public class BookDAO implements
        BaseDAO<Book, BookDTO>{

    private static final String FIND_BY_ID_SQL = "SELECT * FROM book WHERE book_id = ?";
    private static final String FIND_AUTHORS_BY_BOOK_ID_SQL = "SELECT a.* FROM author a JOIN book_author ba ON a.author_id = ba.author_id WHERE ba.book_id = ?";
    private static final String FIND_AUTHOR_IDS_BY_BOOK_ID_SQL = "SELECT author_id FROM book_author WHERE book_id = ?";
    private static final String FIND_COPIES_BY_BOOK_ID_SQL = "SELECT * FROM copies WHERE book_id = ?";
    private static final String FIND_ALL_BOOKS_SQL = "SELECT * FROM book";
    private static final String INSERT_BOOK_SQL = "INSERT INTO book (original_title, original_language, description) VALUES (?, ?, ?)";
    private static final String SET_AUTHOR_SQL = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
    private static final String UPDATE_BOOK_SQL = "UPDATE book SET original_title = ?, original_language = ?, description = ? WHERE book_id = ?";
    private static final String DELETE_BOOK_SQL = "DELETE FROM book WHERE book_id = ?";
    private static final String DELETE_COPY_BY_BOOK_ID_SQL = "DELETE FROM copies WHERE book_id = ?";
    private static final String DELETE_BOOK_AUTHOR_SQL = "DELETE FROM book_author WHERE book_id = ? AND author_id = ?";
    private final AuthorDBMapper authorDBMapper = AuthorDBMapper.INSTANCE;
    private final BookDBMapper bookDBMapper = BookDBMapper.INSTANCE;
    private final CopyDBMapper copyDBMapper = CopyDBMapper.INSTANCE;

    public Optional<Book> findById(Long id) {
        Book book = null;
        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement bookStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            PreparedStatement authorsStatement = connection.prepareStatement(FIND_AUTHORS_BY_BOOK_ID_SQL);
            PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)) {
            bookStatement.setLong(1,id);
            ResultSet bookResultSet = bookStatement.executeQuery();
            if(bookResultSet.next()) {
                book = mapToBook(bookResultSet, authorsStatement, copiesStatement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(book);
    }

    public Set<Book> findAll() {
        Set<Book> bookSet = new HashSet<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement allBooksStatement = connection.prepareStatement(FIND_ALL_BOOKS_SQL);
             PreparedStatement authorsStatement = connection.prepareStatement(FIND_AUTHORS_BY_BOOK_ID_SQL);
             PreparedStatement copiesStatement = connection.prepareStatement(FIND_COPIES_BY_BOOK_ID_SQL)){
            ResultSet booksResultSet = allBooksStatement.executeQuery();
            while (booksResultSet.next()) {
                bookSet.add(mapToBook(booksResultSet, authorsStatement, copiesStatement));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookSet;
    }

    public void create(BookDTO dto) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection()){
            connection.setAutoCommit(false);
            try(PreparedStatement bookStatement = connection.prepareStatement(INSERT_BOOK_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement authorStatement = connection.prepareStatement(SET_AUTHOR_SQL)) {
                bookStatement.setString(1, dto.getOriginalTitle());
                bookStatement.setString(2, Optional.of(dto.getOriginalTitle()).orElse(null));
                bookStatement.setString(3, Optional.of(dto.getDescription()).orElse(null));

                int affectedRows = bookStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating book failed, no rows affected.");
                }
                Long bookId = bookStatement.getGeneratedKeys().getLong(1);
                if (!dto.getAuthorIds().isEmpty()) {
                    for (Long authorId : dto.getAuthorIds()) {
                        authorStatement.setLong(1, bookId);
                        authorStatement.setLong(2, authorId);
                        authorStatement.addBatch();
                    }
                }
                authorStatement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Creating book failed");
        }
    }

    public void update(Long bookId, BookDTO dto) throws SQLException {
        try(Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement bookStatement = connection.prepareStatement(UPDATE_BOOK_SQL);
                PreparedStatement authorsFindStatement = connection.prepareStatement(FIND_AUTHOR_IDS_BY_BOOK_ID_SQL);
                PreparedStatement authorUnsetStatement = connection.prepareStatement(DELETE_BOOK_AUTHOR_SQL);
                PreparedStatement authorSetStatement = connection.prepareStatement(SET_AUTHOR_SQL)) {
                bookStatement.setString(1, dto.getOriginalTitle());
                bookStatement.setString(2, Optional.of(dto.getOriginalTitle()).orElse(null));
                bookStatement.setString(3, Optional.of(dto.getDescription()).orElse(null));
                bookStatement.setLong(4, bookId);
                int affectedRows = bookStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating book failed, no rows affected.");
                }
                authorsFindStatement.setLong(1, bookId);
                ResultSet actualAuthors = authorsFindStatement.executeQuery();
                if (actualAuthors != null || !dto.getAuthorIds().isEmpty()) {
                    updateAuthors(bookId, dto, actualAuthors, authorUnsetStatement, authorSetStatement);
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
            throw new SQLException("Updating book failed");
        }

    }

    public void delete(Long bookId) throws SQLException {
        try(Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement bookDeleteStatement = connection.prepareStatement(DELETE_BOOK_SQL);
                PreparedStatement copyDeleteStatement = connection.prepareStatement(DELETE_COPY_BY_BOOK_ID_SQL)) {
                copyDeleteStatement.setLong(1, bookId);
                copyDeleteStatement.executeUpdate();
                bookDeleteStatement.setLong(1, bookId);
                bookDeleteStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not delete book with id: " + bookId);
        }
    }

    private Book mapToBook(ResultSet bookResultSet, PreparedStatement authorsStatement, PreparedStatement copiesStatement) throws SQLException {
        Book book = bookDBMapper.map(bookResultSet);
        if(book != null) {
            authorsStatement.setLong(1, book.getBookId());
            ResultSet authorsResultSet = authorsStatement.executeQuery();
            Set<Author> authorSet = new HashSet<>();
            while (authorsResultSet.next()) {
                Author author = authorDBMapper.map(authorsResultSet);
                authorSet.add(author);
            }
            if(!authorSet.isEmpty()) {
                book.setAuthors(authorSet);
            }
            copiesStatement.setLong(1, book.getBookId());
            ResultSet copiesResultSet = copiesStatement.executeQuery();
            Set<Copy> copySet = new HashSet<>();
            while (copiesResultSet.next()) {
                Copy copy = copyDBMapper.map(copiesResultSet);
                copySet.add(copy);
            }
            if(!copySet.isEmpty()) {
                book.setCopies(copySet);
            }
        }
        return book;
    }

    private void updateAuthors(Long bookId, BookDTO dto, ResultSet actualAuthors, PreparedStatement authorUnsetStatement, PreparedStatement authorSetStatement) throws SQLException {
        if(actualAuthors != null) {
            Set<Long> matchingAuthors = new HashSet<>();
            while (actualAuthors.next()) {
                Long id = actualAuthors.getLong("author_id");
                if (dto.getAuthorIds() == null || dto.getAuthorIds().isEmpty() || !dto.getAuthorIds().contains(id)) {
                    authorUnsetStatement.setLong(1, bookId);
                    authorUnsetStatement.setLong(2, id);
                    authorUnsetStatement.executeUpdate();
                } else {
                    matchingAuthors.add(id);
                }
            }
            for(Long id : dto.getAuthorIds()) {
                if(!matchingAuthors.contains(id)) {
                    authorSetStatement.setLong(1, bookId);
                    authorSetStatement.setLong(2, id);
                    authorSetStatement.executeUpdate();
                }
            }

        } else {
            for (Long id : dto.getAuthorIds()) {
                authorSetStatement.setLong(1, bookId);
                authorSetStatement.setLong(2, id);
                authorSetStatement.executeUpdate();
            }
        }


    }
}
