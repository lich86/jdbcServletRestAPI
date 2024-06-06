package chervonnaya.service;

import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.mappers.BaseMapper;

public class CopyService extends CrudServiceImpl<Copy, CopyDTO, CopyDAO> {
    public CopyService(CopyDAO repository, Class<Copy> genericType, BaseMapper<Copy, CopyDTO> mapper) {
        super(repository, genericType, mapper);
    }
}
