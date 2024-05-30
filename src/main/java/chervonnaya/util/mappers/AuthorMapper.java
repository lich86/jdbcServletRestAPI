package chervonnaya.util.mappers;

import chervonnaya.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper
public interface AuthorMapper {

    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    @Mapping(target = "authorId", source = "resultSet.getLong('author_id')")
    @Mapping(target = "firstName", source = "resultSet.getString('first_name')")
    @Mapping(target = "lastName", source = "resultSet.getString('last_name')")
    @Mapping(target = "middleName", source = "resultSet.getString('middle_name')")
    @Mapping(target = "penName", source = "resultSet.getString('pen_name')")
    Author map(ResultSet resultSet) throws SQLException;
}