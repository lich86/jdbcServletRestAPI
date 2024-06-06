package chervonnaya.service.mappers;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthorMapper extends BaseMapper<Author, AuthorDTO>{
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    @Mapping(source = "books", target = "bookIds", qualifiedByName = "mapIds")
    AuthorDTO map(Author author);

    @Named("mapIds")
    static Set<Long> mapIds(Set<Book> books) {
        if (books == null) {
            return Collections.emptySet();
        }
        return books.stream().map(Book::getBookId).collect(Collectors.toSet());
    }
}
