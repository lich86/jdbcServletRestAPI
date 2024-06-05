package chervonnaya.service.mappers;

import chervonnaya.dto.BookDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface BookMapper extends BaseMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "copyIds", target = "copies", qualifiedByName = "mapToCopyIds")
    @Mapping(source = "authorIds", target = "authors", qualifiedByName = "mapToAuthorIds")
    BookDTO map(Book book);

    default Set<Long> mapToCopyIds(Set<Copy> copies) {
        return copies.stream().map(Copy::getCopyId).collect(Collectors.toSet());
    }

    default Set<Long> mapToAuthorIds(Set<Author> authors) {
        return authors.stream().map(Author::getAuthorId).collect(Collectors.toSet());
    }
}
