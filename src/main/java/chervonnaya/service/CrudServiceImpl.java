package chervonnaya.service;

import chervonnaya.dao.BaseDAO;
import chervonnaya.dao.exception.CreateEntityException;
import chervonnaya.dao.exception.DeleteEntityException;
import chervonnaya.dao.exception.EntityNotFoundException;
import chervonnaya.dao.exception.UpdateEntityException;
import chervonnaya.dto.BaseDTO;
import chervonnaya.model.BaseEntity;
import chervonnaya.service.mappers.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CrudServiceImpl<E extends BaseEntity, D extends BaseDTO, R extends BaseDAO<E, D>> implements CrudService<D> {
    private final R repository;
    private final Class<E> genericType;
    private final BaseMapper<E, D> mapper;

    private static final Logger logger = LoggerFactory.getLogger(CrudServiceImpl.class);

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
            logger.error("Couldn't find " + genericType.getSimpleName().toLowerCase() + ", id: " + id);
            throw new EntityNotFoundException("Couldn't found " + genericType.getSimpleName().toLowerCase() + ", id: " + id);
        }
    }

    @Override
    public Set<D> getAll() {
        try {
            Set<D> set = repository.findAll().stream().map(mapper::map).collect(Collectors.toSet());
            return set;
        } catch (Exception e) {
            logger.error("Unable to retrieve all" + genericType.getSimpleName().toLowerCase() + "s.");
            throw new EntityNotFoundException("Unable to retrieve all" + genericType.getSimpleName().toLowerCase() + "s", e);
        }

    }

    @Override
    public Long save(D d) {
        try {
            Long id = repository.create(d);
            logger.info("New " + genericType.getSimpleName().toLowerCase() + " is created, id: " + d.getId());
            return id;
        } catch (SQLException e) {
            logger.error("Unable to create new " + genericType.getSimpleName().toLowerCase());
            throw new CreateEntityException("Unable to create new " + genericType.getSimpleName().toLowerCase(), e);
        }

    }

    @Override
    public void update(Long id, D d) {
        try {
            repository.update(id, d);
            logger.info("Updated " + genericType.getSimpleName().toLowerCase() + ", id: " + d.getId());
        } catch (SQLException e) {
            logger.error("Unable to update " + genericType.getSimpleName().toLowerCase()
                    + ", id: " + d.getId(), e);
            throw new UpdateEntityException("Unable to update " + genericType.getSimpleName().toLowerCase()
                    + ", id: " + d.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            repository.delete(id);
            logger.info(genericType.getSimpleName() + ", id: " + id + " is deleted");
        } catch (SQLException e) {
            logger.error("Unable to delete " + genericType.getSimpleName().toLowerCase() + ", id: " + id);
            throw new DeleteEntityException("Unable to delete " + genericType.getSimpleName().toLowerCase() + ", id: " + id, e);
        }
    }
}
