package chervonnaya.service;

import chervonnaya.dao.BaseDAO;
import chervonnaya.dto.BaseDTO;
import chervonnaya.exception.DatabaseOperationException;
import chervonnaya.exception.EntityNotFoundException;
import chervonnaya.model.BaseEntity;
import chervonnaya.service.mappers.BaseMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CrudServiceImplTest {

    @Mock
    private BaseDAO<BaseEntity, BaseDTO> mockRepository;

    @Mock
    private BaseMapper<BaseEntity, BaseDTO> mockMapper;

    @InjectMocks
    private CrudServiceImpl<BaseEntity, BaseDTO, BaseDAO<BaseEntity, BaseDTO>> crudService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_Should_Succeed() {
        Long id = 1L;
        BaseEntity entity = new BaseEntity(id);
        BaseDTO dto = new BaseDTO(id);
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.of(entity));
        Mockito.when(mockMapper.map(entity)).thenReturn(dto);

        Optional<BaseDTO> result = crudService.getById(id);

        Assertions.assertEquals(dto, result.get());
        Mockito.verify(mockRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mockMapper, Mockito.times(1)).map(entity);
    }

    @Test
    public void getById_Should_Fail() {
        Long id = 1L;
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> crudService.getById(id));

        Mockito.verify(mockRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mockMapper, Mockito.never()).map(Mockito.any());
    }

    @Test
    public void getAll_Should_Succeed() {
        Set<BaseEntity> entities = new HashSet<>();
        entities.add(new BaseEntity(1L));
        entities.add(new BaseEntity(2L));

        Mockito.when(mockRepository.findAll()).thenReturn(entities);
        Mockito.when(mockMapper.map(Mockito.any(BaseEntity.class))).thenAnswer(
                invocation -> {
                    BaseEntity entity = invocation.getArgument(0);
                    return new BaseDTO(entity.getId());
                });
        Set<BaseDTO> DTOs = crudService.getAll();

        assertEquals(entities.size(), DTOs.size());
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
        Mockito.verify(mockMapper, Mockito.times(entities.size())).map(Mockito.any(BaseEntity.class));
    }

    @Test
    public void testSave_Should_Succeed() throws SQLException {
        BaseDTO dto = new BaseDTO(null);
        Mockito.when(mockRepository.create(dto)).thenReturn(1L);

        Long id = crudService.save(dto);

        Assertions.assertEquals(1L, id);
        Mockito.verify(mockRepository, Mockito.times(1)).create(dto);
    }

    @Test
    public void testDelete_Success() throws SQLException {
        Long id = 1L;

        Assertions.assertDoesNotThrow(() -> crudService.delete(id));

        Mockito.verify(mockRepository, Mockito.times(1)).delete(id);
    }

    @Test
    public void testSave_Should_ThrowException() throws SQLException {
        BaseDTO dto = new BaseDTO(null);
        Mockito.when(mockRepository.create(dto)).thenThrow(SQLException.class);

        Assertions.assertThrows(DatabaseOperationException.class, () -> crudService.save(dto));

        Mockito.verify(mockRepository, Mockito.times(1)).create(dto);
    }


}