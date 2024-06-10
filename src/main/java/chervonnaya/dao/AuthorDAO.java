package chervonnaya.dao;

import chervonnaya.dao.exception.DatabaseOperationException;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.util.ConnectionManager;
import chervonnaya.dao.mappers.AuthorDBMapper;
import chervonnaya.dao.mappers.BookDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class AuthorDAO implements BaseDAO<Author, AuthorDTO> {
    private static final String FIND_BY_ID_SQL = "SELECT * FROM author WHERE author_id = ?";
    private static final String FIND_BOOKS_BY_AUTHOR_ID_SQL = "SELECT * FROM book b JOIN book_author ba ON b.book_id = ba.book_id WHERE author_id = ?";
    private static final String FIND_ALL_AUTHORS = "SELECT * FROM author";
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO author (first_name, last_name, middle_name, pen_name) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_AUTHOR_SQL = "UPDATE author SET first_name = ?, last_name = ?, middle_name = ?, pen_name = ? WHERE author_id = ?";
    private static final String DELETE_AUTHOR_SQL = "DELETE FROM author WHERE author_id = ?";
    private static final String DELETE_BOOK_SQL = "DELETE FROM book WHERE book_id = ?";
    private static final String DELETE_COPY_BY_BOOK_ID_SQL = "DELETE FROM copies WHERE book_id = ?";
    private static final String DELETE_BOOK_AUTHOR_SQL = "DELETE FROM book_author WHERE author_id = ?";
    private final AuthorDBMapper authorDBMapper = AuthorDBMapper.INSTANCE;
    private final BookDBMapper bookDBMapper = BookDBMapper.INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(AuthorDAO.class);


    @Override
    public Optional<Author> findById(Long authorId) {
        Author author = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(FIND_BY_ID_SQL);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL)) {

            authorStatement.setLong(1, authorId);
            ResultSet authorResultSet = authorStatement.executeQuery();
            if (authorResultSet.next()) {
                author = mapToAuthor(authorResultSet, booksStatement);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Unable to retrieve author, id: " + authorId, e);
        }
        return Optional.ofNullable(author);
    }


    @Override
    public Set<Author> findAll(){
        Set<Author> authorSet = new HashSet<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement allAuthorsStatement = connection.prepareStatement(FIND_ALL_AUTHORS);
             PreparedStatement booksStatement = connection.prepareStatement(FIND_BOOKS_BY_AUTHOR_ID_SQL)) {
            ResultSet authorResultSet = allAuthorsStatement.executeQuery();
            while (authorResultSet.next()) {
                authorSet.add(mapToAuthor(authorResultSet, booksStatement));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Unable to retrieve authors", e);
        }
        return authorSet;

    }

    public Long create(AuthorDTO dto) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(INSERT_AUTHOR_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());
            if(dto.getMiddleName() != null) {
                authorStatement.setString(3, dto.getMiddleName());
            } else {
                authorStatement.setNull(3, Types.NULL);
            }
            if(dto.getPenName() != null) {
                authorStatement.setString(4, dto.getPenName());
            } else {
                authorStatement.setNull(4, Types.NULL);
            }

            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseOperationException("Creating author failed, no rows affected.");
            }
            ResultSet authorResult = authorStatement.getGeneratedKeys();
            authorResult.next();
            return authorResult.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseOperationException("Creating author failed", e);
        }
    }

    public void update(Long authorId, AuthorDTO dto) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement authorStatement = connection.prepareStatement(UPDATE_AUTHOR_SQL)) {
            authorStatement.setString(1, dto.getFirstName());
            authorStatement.setString(2, dto.getLastName());
            if(dto.getMiddleName() != null) {
                authorStatement.setString(3, dto.getMiddleName());
            } else {
                authorStatement.setNull(3, Types.NULL);
            }
            if(dto.getPenName() != null) {
                authorStatement.setString(4, dto.getPenName());
            } else {
                authorStatement.setNull(4, Types.NULL);
            }
            authorStatement.setLong(5, authorId);

            int affectedRows = authorStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseOperationException("Updating author failed, no rows affected, id:" + authorId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Updating author failed, id:" + authorId, e);
        }

    }

    public void delete(Long authorId) {
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
                    long bookId = booksToDelete.getLong("book_id");
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
                    throw new DatabaseOperationException("No author found, id: " + authorId);
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseOperationException("Could not delete author, id: " + authorId, e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Could not delete author, id: " + authorId, e);
        }

    }

    private Author mapToAuthor(ResultSet authorResultSet, PreparedStatement booksStatement) throws SQLException {
        Author author = authorDBMapper.map(authorResultSet);

        if (author != null) {
            booksStatement.setLong(1, author.getId());
            ResultSet booksResultSet = booksStatement.executeQuery();
            Set<Book> bookSet = new HashSet<>();
            while (booksResultSet.next()) {
                try {
                    Book book = bookDBMapper.map(booksResultSet);
                    bookSet.add(book);
                } catch (SQLException e) {
                    logger.error("Unable to retrieve books for author:" + author.getId());
                }
            }
            if (!bookSet.isEmpty()) {
                author.setBooks(bookSet);
            }
        }
        return author;
    }

}