package chervonnaya.service;

import chervonnaya.dao.AuthorDAO;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.service.mappers.BaseMapper;

public class AuthorServiceImpl extends CrudServiceImpl<Author, AuthorDTO, AuthorDAO> {
    public AuthorServiceImpl(AuthorDAO repository, Class<Author> genericType, BaseMapper<Author, AuthorDTO> mapper) {
        super(repository, genericType, mapper);
    }
}
