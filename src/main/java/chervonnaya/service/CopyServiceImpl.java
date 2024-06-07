package chervonnaya.service;

import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.mappers.BaseMapper;

public class CopyServiceImpl extends CrudServiceImpl<Copy, CopyDTO, CopyDAO> {
    public CopyServiceImpl(CopyDAO repository, Class<Copy> genericType, BaseMapper<Copy, CopyDTO> mapper) {
        super(repository, genericType, mapper);
    }
}
