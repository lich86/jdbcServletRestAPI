package chervonnaya;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.dto.BookDTO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.enums.Language;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;


public class TestData {
    public static final String AUTHOR_PEN_NAME = "Сомерсет Моэм";
    public static final AuthorDTO AUTHOR_DTO = new AuthorDTO();
    public static final AuthorDTO WRONG_AUTHOR_DTO = new AuthorDTO();

    public static final String BOOK_DESCRIPTION = "Описание книги.";
    public static final BookDTO BOOK_DTO = new BookDTO();
    public static final BookDTO WRONG_BOOK_DTO = new BookDTO();

    public static final String COPY_PUBLISHING_HOUSE = "Clever";
    public static final CopyDTO COPY_DTO = new CopyDTO();
    public static final CopyDTO WRONG_COPY_DTO = new CopyDTO();


    static {
        AUTHOR_DTO.setFirstName("Антон");
        AUTHOR_DTO.setMiddleName("Павлович");
        AUTHOR_DTO.setLastName("Чехов");
        AUTHOR_DTO.setPenName("Антоша Чехонте");
        AUTHOR_DTO.setBookTitles(new HashSet<>());
        AUTHOR_DTO.setBookIds(new HashSet<>());

        BOOK_DTO.setOriginalTitle("Вишнёвый сад");
        BOOK_DTO.setOriginalLanguage(Language.RUSSIAN);
        BOOK_DTO.setDescription("Пьеса в четырёх действиях Антона Павловича Чехова, жанр которой сам автор определил как комедия.");
        BOOK_DTO.setAuthorIds(new HashSet<>(Arrays.asList(1L)));
        BOOK_DTO.setCopyIds(new HashSet<>());

        COPY_DTO.setTitle("Вишнёвый сад");
        COPY_DTO.setLanguage(Language.RUSSIAN);
        COPY_DTO.setPrice(300.00);
        COPY_DTO.setPublishingHouse("АСТрель");
        COPY_DTO.setPublishingYear(Year.of(2002));
        COPY_DTO.setBookId(1L);
    }
}
