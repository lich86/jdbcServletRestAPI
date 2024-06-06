package chervonnaya.service.mappers;

import chervonnaya.dto.BookDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BookMapper extends BaseMapper<Book, BookDTO> {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "copies", target = "copyIds", qualifiedByName = "mapCopyIds")
    @Mapping(source = "authors", target = "authorIds", qualifiedByName = "mapAuthorIds")
    BookDTO map(Book book);

    @Named("mapCopyIds")
    static Set<Long> mapCopyIds(Set<Copy> copies) {
        if (copies == null) {
            return Collections.emptySet();
        }
        return copies.stream().map(Copy::getCopyId).collect(Collectors.toSet());
    }

    @Named("mapAuthorIds")
    static Set<Long> mapAuthorIds(Set<Author> authors) {
        return authors.stream().map(Author::getAuthorId).collect(Collectors.toSet());
    }
}
