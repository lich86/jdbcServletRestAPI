package chervonnaya.util.mappers;

import chervonnaya.model.Book;
import chervonnaya.model.enums.Language;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "bookId", source = "resultSet.getLong('book_id')")
    @Mapping(target = "description", source = "resultSet.getString('description')")
    @Mapping(target = "originalLanguage", source = "resultSet.getString('original_language')", qualifiedByName = "mapToLanguage")
    @Mapping(target = "originalTitle", source = "resultSet.getString('original_title')")
    Book map(ResultSet resultSet) throws SQLException;

    default Language mapToLanguage(String languageString) {
        return Language.valueOf(languageString);
    }
}
