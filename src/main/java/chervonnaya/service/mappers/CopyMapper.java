package chervonnaya.service.mappers;

import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CopyMapper extends BaseMapper<Copy, CopyDTO> {
    CopyMapper INSTANCE = Mappers.getMapper(CopyMapper.class);

    @Mapping(source = "book", target = "bookId", qualifiedByName = "mapId")
    CopyDTO map(Copy copy);

    @Named("mapId")
    static Long mapId(Book book) {
        return book.getBookId();
    }
}
