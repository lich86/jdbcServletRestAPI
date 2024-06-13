package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.mappers.CopyMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CopyDAOTest extends BaseIntegrationTest {
    private final CopyDAO copyDAO = new CopyDAO(dataSource);
    private final CopyMapper toDTOMapper = CopyMapper.INSTANCE;

    @Test
    void findCopyById_Should_ReturnCorrectCopy() {
        CopyDTO expextedCopyDTO = TestData.COPY_DTO;
        Long id = copyDAO.create(expextedCopyDTO);
        expextedCopyDTO.setId(id);

        Copy copy = copyDAO.findById(id).orElse(null);

        CopyDTO actualCopyDTO = toDTOMapper.map(copy);
        Assertions.assertEquals(expextedCopyDTO, actualCopyDTO);
    }

    @Test
    void findCopys_Should_ReturnCopySet() {
        Set<Copy> copys = copyDAO.findAll();

        assertNotNull(copys);
    }

    @Test
    void createCopy_Should_CreateEntityInDB() {
        Long id = copyDAO.create(TestData.COPY_DTO);

        assertTrue(id > 0);
        Optional<Copy> optionalCopy = copyDAO.findById(id);
        assertTrue(optionalCopy.isPresent());
    }

    @Test
    void updateCopy_Should_UpdateEntityAsExpected() {
        Copy copy = copyDAO.findById(1L).orElse(null);

        Assertions.assertNotEquals(copy.getPublishingHouse(), TestData.COPY_PUBLISHING_HOUSE);

        CopyDTO copyDTO = toDTOMapper.map(copy);
        copyDTO.setPublishingHouse(TestData.COPY_PUBLISHING_HOUSE);
        copyDAO.update(1L, copyDTO);
        copy = copyDAO.findById(1L).orElse(null);

        Assertions.assertEquals(TestData.COPY_PUBLISHING_HOUSE, copy.getPublishingHouse());

    }

    @Test
    void deleteCopy_Should_DeleteCopyFromDB() {
        copyDAO.delete(1L);

        Assertions.assertTrue(copyDAO.findById(1L).isEmpty());
    }

    @Test
    void findById_Should_ReturnEmptyOptionalIfWrongId() {
        Assertions.assertTrue(copyDAO.findById(99L).isEmpty());
    }


}