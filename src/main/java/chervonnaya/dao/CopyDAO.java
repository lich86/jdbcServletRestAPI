package chervonnaya.dao;

import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import chervonnaya.util.ConnectionManager;
import chervonnaya.util.mappers.BookMapper;
import chervonnaya.util.mappers.CopyMapper;

import java.sql.*;
import java.util.*;

public class CopyDAO {
    private static final String FIND_BY_ID_SQL = "SELECT * FROM copies WHERE copy_id = ?";
    private static final String FIND_ALL_COPIES = "SELECT * FROM copies";
    private static final String FIND_BOOK_BY_COPY_ID_SQL = "SELECT * FROM book b JOIN copies c ON b.id = c.book_id WHERE copy_id = ?";
    private static final String INSERT_COPY_SQL = "INSERT INTO copies (title, language, price, publishingHouse, publishingYear, translator, book_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_COPY_SQL = "UPDATE copies SET title = ?, language = ?, price = ?, publishingHouse = ?, publishingYear = ?, translator = ?, book_id = ? WHERE copy_id = ?";
    private static final String DELETE_COPY_SQL = "DELETE FROM copies WHERE copy_id = ?";
    private final CopyMapper copyMapper = CopyMapper.INSTANCE;
    private final BookMapper bookMapper = BookMapper.INSTANCE;

    public Optional<Copy> findById(Long copyId) {
        Copy copy = null;
        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement copyStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            PreparedStatement bookStatement = connection.prepareStatement(FIND_BOOK_BY_COPY_ID_SQL)) {
            copyStatement.setLong(1, copyId);
            ResultSet copyResultSet = copyStatement.executeQuery();
            bookStatement.setLong(1, copyId);
            ResultSet bookResultSet = bookStatement.executeQuery();
            if(copyResultSet.next()) {
                copy = copyMapper.map(copyResultSet);
                bookResultSet.next();
                Book book = bookMapper.map(bookResultSet);
                copy.setBook(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(copy);
    }

    public Set<Copy> findAll() {
        Set<Copy> copySet = new HashSet<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement copyStatement = connection.prepareStatement(FIND_ALL_COPIES);
             PreparedStatement bookStatement = connection.prepareStatement(FIND_BOOK_BY_COPY_ID_SQL)){
            ResultSet copyResultSet = copyStatement.executeQuery();
            while (copyResultSet.next()) {
                Copy copy = copyMapper.map(copyResultSet);
                bookStatement.setLong(1, copy.getCopyId());
                ResultSet bookResultSet = bookStatement.executeQuery();
                bookResultSet.next();
                Book book = bookMapper.map(bookResultSet);
                copy.setBook(book);
                copySet.add(copy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return copySet;
    }

    public void create(CopyDTO dto) {
        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement copyStatement = connection.prepareStatement(INSERT_COPY_SQL)) {
            copyStatement.setString(1, dto.getTitle());
            copyStatement.setString(2, dto.getLanguage().name());
            copyStatement.setDouble(3, dto.getPrice());
            copyStatement.setString(4, Optional.of(dto.getPublishingHouse()).orElse(null));
            copyStatement.setString(5, Optional.of(dto.getPublishingYear().toString()).orElse(null));
            copyStatement.setString(6, Optional.of(dto.getTranslator()).orElse(null));
            copyStatement.setLong(7, dto.getBookId());

            int affectedRows = copyStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating copy failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Long copyId, CopyDTO dto) {
        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement copyStatement = connection.prepareStatement(UPDATE_COPY_SQL)) {
            copyStatement.setString(1, dto.getTitle());
            copyStatement.setString(2, dto.getLanguage().name());
            copyStatement.setDouble(3, dto.getPrice());
            copyStatement.setString(4, Optional.of(dto.getPublishingHouse()).orElse(null));
            copyStatement.setString(5, Optional.of(dto.getPublishingYear().toString()).orElse(null));
            copyStatement.setString(6, Optional.of(dto.getTranslator()).orElse(null));
            copyStatement.setLong(7, dto.getBookId());
            copyStatement.setLong(8, copyId);

            int affectedRows = copyStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating copy failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long copyId) {
        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement copyDeleteStatement = connection.prepareStatement(DELETE_COPY_SQL)) {
            copyDeleteStatement.setLong(1, copyId);
            int affectedRows = copyDeleteStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No copy found with id " + copyId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
