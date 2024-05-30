package chervonnaya.model;

import chervonnaya.model.enums.Language;

import java.time.Year;
import java.util.Set;

public class Copy {
    private Long copyId;
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
        this.copyId = copyId;
        this.title = title;
        this.language = language;
        this.price = price;
        this.publishingHouse = publishingHouse;
        this.publishingYear = publishingYear;
        this.translator = translator;
        this.book = book;
    }

    public Long getCopyId() {
        return copyId;
    }

    public void setCopyId(Long copyId) {
        this.copyId = copyId;
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
}
