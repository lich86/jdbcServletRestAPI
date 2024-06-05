package chervonnaya.dto;

import chervonnaya.model.enums.Language;

import java.time.Year;

public class CopyDTO extends BaseDTO{
    private String title;
    private Language language;
    private Double price;
    private String publishingHouse;
    private Year publishingYear;
    private String translator;
    private Long bookId;

    public CopyDTO() {

    }

    public CopyDTO(String title, Language language, Double price, String publishingHouse, Year publishingYear, String translator, Long bookId) {
        this.title = title;
        this.language = language;
        this.price = price;
        this.publishingHouse = publishingHouse;
        this.publishingYear = publishingYear;
        this.translator = translator;
        this.bookId = bookId;
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

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
