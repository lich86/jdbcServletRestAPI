package chervonnaya.model;

import chervonnaya.model.enums.Language;

import java.time.Year;
import java.util.Objects;

public class Copy extends BaseEntity {
    private String title;
    private Language language;
    private Double price;
    private String publishingHouse;
    private Year publishingYear;
    private String translator;
    private Book book;

    public Copy() {

    }

    public Copy(Long copyId, String title, Language language, Double price, String publishingHouse, Year publishingYear, String translator, Book book) {
        super(copyId);
        this.title = title;
        this.language = language;
        this.price = price;
        this.publishingHouse = publishingHouse;
        this.publishingYear = publishingYear;
        this.translator = translator;
        this.book = book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public Year getPublishingYear() {
        return publishingYear;
    }

    public void setPublishingYear(Year publishingYear) {
        this.publishingYear = publishingYear;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Copy copy = (Copy) o;
        return title.equals(copy.title) && language == copy.language && price.equals(copy.price) && Objects.equals(publishingHouse, copy.publishingHouse) && Objects.equals(publishingYear, copy.publishingYear) && Objects.equals(translator, copy.translator) && book.equals(copy.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, language, price, publishingHouse, publishingYear, translator, book);
    }
}
