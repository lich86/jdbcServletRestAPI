package chervonnaya.service.mappers;

import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CopyMapper extends BaseMapper {
    CopyMapper INSTANCE = Mappers.getMapper(CopyMapper.class);

    @Mapping(source = "book", target = "book_id", qualifiedByName = "mapToId")
    CopyDTO map(Copy copy);

    default Long mapToId(Copy copy) {
        return copy.getCopyId();
    }
}
