package chervonnaya.dao;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.TestData;
import chervonnaya.model.Copy;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CopyDAOTest extends BaseIntegrationTest {

    CopyDAO copyDAO = new CopyDAO(dataSource);

    @Test
    void create() {
        Long id = copyDAO.create(TestData.COPY_DTO);
        assertTrue(id > 0);

        Optional<Copy> optionalAuthor = copyDAO.findById(id);
        assertTrue(optionalAuthor.isPresent());
    }
}