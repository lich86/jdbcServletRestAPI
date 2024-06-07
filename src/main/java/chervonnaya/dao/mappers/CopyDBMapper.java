package chervonnaya.dao.mappers;

import chervonnaya.model.Copy;
import chervonnaya.model.enums.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CopyDBMapper {
    CopyDBMapper INSTANCE = Mappers.getMapper(CopyDBMapper.class);

    @Mapping(target = "id", source = "resultSet", qualifiedByName = "mapCopyId")
    @Mapping(target = "title", source = "resultSet", qualifiedByName = "mapTitle")
    @Mapping(target = "language", source = "resultSet", qualifiedByName = "mapLanguage")
    @Mapping(target = "price", source = "resultSet", qualifiedByName = "mapPrice")
    @Mapping(target = "publishingHouse", source = "resultSet", qualifiedByName = "mapPublishingHouse")
    @Mapping(target = "publishingYear", source = "resultSet", qualifiedByName = "mapPublishingYear")
    @Mapping(target = "translator", source = "resultSet", qualifiedByName = "mapTranslator")
    Copy map(ResultSet resultSet) throws SQLException;

    @Named("mapCopyId")
    static Long mapCopyId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("copy_id");
    }

    @Named("mapTitle")
    static String mapTitle(ResultSet resultSet) throws SQLException {
        return resultSet.getString("title");
    }

    @Named("mapLanguage")
    static Language mapLanguage(ResultSet resultSet) throws SQLException {
        return Language.valueOf(resultSet.getString("language"));
    }

    @Named("mapPrice")
    static Double mapPrice(ResultSet resultSet) throws SQLException {
        return resultSet.getDouble("price");
    }

    @Named("mapPublishingHouse")
    static String mapPublishingHouse(ResultSet resultSet) throws SQLException {
        return resultSet.getString("publishing_house");
    }

    @Named("mapPublishingYear")
    static Year mapYear(ResultSet resultSet) throws SQLException {
        return Year.of(resultSet.getInt("publishing_year"));
    }

    @Named("mapTranslator")
    static String mapTranslator(ResultSet resultSet) throws SQLException {
        return resultSet.getString("translator");
    }
}
