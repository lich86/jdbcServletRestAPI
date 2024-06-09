package chervonnaya.dao;

import chervonnaya.dto.BaseDTO;
import chervonnaya.model.BaseEntity;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public interface BaseDAO<E extends BaseEntity, D extends BaseDTO> {
    Optional<E> findById(Long id);
    Set<E> findAll();
    Long create(D d) throws SQLException;
    void update(Long id, D d) throws SQLException;
    void delete(Long id) throws SQLException;



}
