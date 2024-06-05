package chervonnaya.model;

import chervonnaya.model.enums.Language;

import java.util.Set;

public class Book extends BaseEntity {
    private Long bookId;
    private String originalTitle;
    private Language originalLanguage;
    private String description;
    private Set<Copy> copies;
    private Set<Author> authors;

    public Book() {

    }

    public Book(Long bookId, String originalTitle, Language originalLanguage, String description, Set<Copy> copies, Set<Author> authors) {
        this.bookId = bookId;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.description = description;
        this.copies = copies;
        this.authors = authors;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Copy> getCopies() {
        return copies;
    }

    public void setCopies(Set<Copy> copies) {
        this.copies = copies;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
}
