package chervonnaya.dao.mappers;

import chervonnaya.model.Book;
import chervonnaya.model.enums.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper
public interface BookDBMapper {
    BookDBMapper INSTANCE = Mappers.getMapper(BookDBMapper.class);

    @Mapping(target = "bookId", source = "resultSet.getLong('book_id')")
    @Mapping(target = "description", source = "resultSet.getString('description')")
    @Mapping(target = "originalLanguage", source = "resultSet.getString('original_language')", qualifiedByName = "mapToLanguage")
    @Mapping(target = "originalTitle", source = "resultSet.getString('original_title')")
    Book map(ResultSet resultSet) throws SQLException;

    default Language mapToLanguage(String languageString) {
        return Language.valueOf(languageString);
    }
}
