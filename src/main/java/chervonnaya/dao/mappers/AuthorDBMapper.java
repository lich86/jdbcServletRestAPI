package chervonnaya.dao.mappers;

import chervonnaya.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthorDBMapper {

    AuthorDBMapper INSTANCE = Mappers.getMapper(AuthorDBMapper.class);

    @Mapping(target = "id", source = "resultSet", qualifiedByName = "mapAuthorId")
    @Mapping(target = "firstName", source = "resultSet", qualifiedByName = "mapFirstName")
    @Mapping(target = "lastName", source = "resultSet", qualifiedByName = "mapLastName")
    @Mapping(target = "middleName", source = "resultSet", qualifiedByName = "mapMiddleName")
    @Mapping(target = "penName", source = "resultSet", qualifiedByName = "mapPenName")
    Author map(ResultSet resultSet) throws SQLException;

    @Named("mapAuthorId")
    static Long mapAuthorId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("author_id");
    }

    @Named("mapFirstName")
    static String mapFirstName(ResultSet resultSet) throws SQLException {
        return resultSet.getString("first_name");
    }

    @Named("mapLastName")
    static String mapLastName(ResultSet resultSet) throws SQLException {
        return resultSet.getString("last_name");
    }

    @Named("mapMiddleName")
    static String mapMiddleName(ResultSet resultSet) throws SQLException {
        return resultSet.getString("middle_name");
    }

    @Named("mapPenName")
    static String mapPenName(ResultSet resultSet) throws SQLException {
        return resultSet.getString("pen_name");
    }
}