package chervonnaya.servlet;

import chervonnaya.BaseIntegrationTest;
import chervonnaya.dto.AuthorDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServletTestData extends BaseIntegrationTest {
    //private static Integer containerPort = TOMCAT_CONTAINER.getMappedPort(8080);
    public static final String AUTHOR_URL = "http://localhost:8080/author";
    public static final String AUTHOR_PEN_NAME = "Сомерсет Моэм";
    public static final String ADMIN_MAIL = "qwerty@qwerty.com";
    public static final Set<String> BOOKS = new HashSet<>(Arrays.asList("Каштанка", "Толстый и тонкий"));
    public static final AuthorDTO AUTHOR_DTO = new AuthorDTO();

    static {
        AUTHOR_DTO.setFirstName("Антон");
        AUTHOR_DTO.setMiddleName("Павлович");
        AUTHOR_DTO.setLastName("Чехов");
        AUTHOR_DTO.setPenName("Антоша Чехонте");
        AUTHOR_DTO.setBookTitles(BOOKS);
    }



}
