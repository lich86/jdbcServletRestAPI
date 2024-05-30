package chervonnaya.util.mappers;

import chervonnaya.model.Copy;
import chervonnaya.model.enums.Language;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public interface CopyMapper {
    CopyMapper INSTANCE = Mappers.getMapper(CopyMapper.class);

    @Mapping(target = "copyId", source = "resultSet.getLong('copy_id')")
    @Mapping(target = "title", source = "resultSet.getString('title')")
    @Mapping(target = "language", source = "resultSet.getString('language')", qualifiedByName = "mapToLanguage")
    @Mapping(target = "price", source = "resultSet.getDouble('price')")
    @Mapping(target = "publishingHouse", source = "resultSet.getString('publishing_house')")
    @Mapping(target = "publishingYear", source = "resultSet.getString('publishing_year')", qualifiedByName = "mapToYear")
    @Mapping(target = "translator", source = "resultSet.getString('translator')")
    Copy map(ResultSet resultSet) throws SQLException;

    default Language mapToLanguage(String languageString) {
        return Language.valueOf(languageString);
    }
    default Year mapToYear(String yearString) {
        return Year.parse(yearString);
    }
}
