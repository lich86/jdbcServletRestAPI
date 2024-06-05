package chervonnaya.service.mappers;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface AuthorMapper extends BaseMapper{
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    @Mapping(source = "books", target = "bookIds", qualifiedByName = "mapToIds")
    AuthorDTO map(Author author);

    default Set<Long> mapToIds(Set<Book> books) {
       return books.stream().map(Book::getBookId).collect(Collectors.toSet());
    }
}
