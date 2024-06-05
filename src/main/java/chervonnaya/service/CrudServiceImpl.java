package chervonnaya.service;

import chervonnaya.dao.BaseDAO;
import chervonnaya.dto.BaseDTO;
import chervonnaya.model.BaseEntity;
import chervonnaya.service.mappers.BaseMapper;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CrudServiceImpl<E extends BaseEntity, D extends BaseDTO, R extends BaseDAO<E, D>> implements CrudService<D> {
    private final R repository;
    private final Class<E> genericType;
    private final BaseMapper<E, D> mapper;

    public CrudServiceImpl(R repository, Class<E> genericType, BaseMapper<E, D> mapper) {
        this.repository = repository;
        this.genericType = genericType;
        this.mapper = mapper;
    }

    @Override
    public Optional<D> getById(Long id) {
        Optional<E> entity = repository.findById(id);
        if(entity.isPresent()) {
            return Optional.of(mapper.map(entity.get()));
        } else {
            //TODO add logger and exceptions
            throw new RuntimeException();
        }
    }

    @Override
    public Set<D> getAll() {
        Set<D> set = repository.findAll().stream().map(mapper::map).collect(Collectors.toSet());
        //TODO logger
        return set;
    }

    @Override
    public void save(D d) {
        try {
            repository.create(d);
            //TODO logger
        } catch (SQLException e) {
            //TODO logger
            e.printStackTrace();
        }

    }

    @Override
    public void update(Long id, D d) {
        try {
            repository.update(id, d);
            //TODO logger
        } catch (SQLException e) {
            //TODO logger
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        try {
            repository.delete(id);
            //TODO logger
        } catch (SQLException e) {
            //TODO logger
            e.printStackTrace();
        }
    }
}
