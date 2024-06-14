package chervonnaya;

import chervonnaya.dto.AuthorDTO;
import chervonnaya.dto.BookDTO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Author;
import chervonnaya.model.Book;
import chervonnaya.model.Copy;
import chervonnaya.model.enums.Language;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;


public class TestData {
    public static final Author AUTHOR1 = new Author();
    public static final Author AUTHOR2 = new Author();
    public static final AuthorDTO AUTHOR_DTO = new AuthorDTO();
    public static final Book BOOK1 = new Book();
    public static final Book BOOK2 = new Book();
    public static final BookDTO BOOK_DTO = new BookDTO();
    public static final Copy COPY1 = new Copy();
    public static final Copy COPY2 = new Copy();
    public static final CopyDTO COPY_DTO = new CopyDTO();

    static {
        AUTHOR1.setId(1L);
        AUTHOR1.setFirstName("Антон");
        AUTHOR1.setMiddleName("Павлович");
        AUTHOR1.setLastName("Чехов");
        AUTHOR1.setPenName("Антоша Чехонте");
        AUTHOR1.setBooks(new HashSet<>());

        AUTHOR2.setId(2L);
        AUTHOR2.setFirstName("Джек");
        AUTHOR2.setLastName("Лондон");
        AUTHOR2.setBooks(new HashSet<>());

        BOOK1.setId(1L);
        BOOK1.setOriginalTitle("Вишнёвый сад");
        BOOK1.setOriginalLanguage(Language.RUSSIAN);
        BOOK1.setDescription("Пьеса в четырёх действиях Антона Павловича Чехова, жанр которой сам автор определил как комедия.");
        BOOK1.setCopies(new HashSet<>());

        BOOK2.setId(2L);
        BOOK2.setOriginalTitle("Человек в футляре");
        BOOK2.setOriginalLanguage(Language.RUSSIAN);
        BOOK2.setDescription("Историю об одиноком по натуре человеке, который старался спрятаться от жизни в свою скорлупу, рассказывает на охотничьем ночлеге учитель гимназии Буркин своему товарищу, ветеринарному врачу Ивану Чимша-Гималайскому.");
        BOOK2.setCopies(new HashSet<>());

        COPY1.setId(1L);
        COPY1.setTitle("Вишнёвый сад");
        COPY1.setLanguage(Language.RUSSIAN);
        COPY1.setPrice(300.00);
        COPY1.setPublishingHouse("АСТрель");
        COPY1.setPublishingYear(Year.of(2002));
        COPY1.setBook(new Book());

        COPY2.setId(2L);
        COPY2.setTitle("Вишнёвый сад");
        COPY2.setLanguage(Language.RUSSIAN);
        COPY2.setPrice(320.00);
        COPY2.setPublishingHouse("Clever");
        COPY2.setPublishingYear(Year.of(2022));
        COPY2.setBook(new Book());

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
