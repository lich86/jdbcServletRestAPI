package chervonnaya.dao.mappers;

import chervonnaya.model.Book;
import chervonnaya.model.enums.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BookDBMapper {
    BookDBMapper INSTANCE = Mappers.getMapper(BookDBMapper.class);

    @Mapping(target = "id", source = "resultSet", qualifiedByName = "mapBookId")
    @Mapping(target = "description", source = "resultSet", qualifiedByName = "mapDescription")
    @Mapping(target = "originalLanguage", source = "resultSet", qualifiedByName = "mapOriginalLanguage")
    @Mapping(target = "originalTitle", source = "resultSet", qualifiedByName = "mapOriginalTitle")
    Book map(ResultSet resultSet) throws SQLException;

    @Named("mapBookId")
    static Long mapBookId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("book_id");
    }

    @Named("mapDescription")
    static String mapDescription(ResultSet resultSet) throws SQLException {
        return resultSet.getString("description");
    }

    @Named("mapOriginalLanguage")
    static Language mapOriginalLanguage(ResultSet resultSet) throws SQLException {
        return Language.valueOf(resultSet.getString("original_language"));
    }

    @Named("mapOriginalTitle")
    static String mapOriginalTitle(ResultSet resultSet) throws SQLException {
        return resultSet.getString("original_title");
    }
}
