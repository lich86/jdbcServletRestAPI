package chervonnaya.service;

import chervonnaya.dto.BaseDTO;

import java.util.Optional;
import java.util.Set;

public interface CrudService <D extends BaseDTO> {
    Optional<D> getById(Long id);

    Set<D> getAll();

    Long save(D d);

    void update(Long id, D d);

    void delete(Long id);




}
