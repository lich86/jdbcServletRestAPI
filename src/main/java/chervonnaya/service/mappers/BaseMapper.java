package chervonnaya.service.mappers;

import chervonnaya.dto.BaseDTO;
import chervonnaya.model.BaseEntity;

public interface BaseMapper<E extends BaseEntity, D extends BaseDTO> {
    D map(E entity);
}