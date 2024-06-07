package chervonnaya.dto;

import chervonnaya.model.enums.Language;

import java.util.Set;

public class BookDTO extends BaseDTO{
    private String originalTitle;
    private Language originalLanguage;
    private String description;
    private Set<Long> copyIds;
    private Set<Long> authorIds;

    public BookDTO() {

    }

    public BookDTO(Long bookId, String originalTitle, Language originalLanguage, String description, Set<Long> copyIds, Set<Long> authorIds) {
        super(bookId);
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.description = description;
        this.copyIds = copyIds;
        this.authorIds = authorIds;
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

    public Set<Long> getCopyIds() {
        return copyIds;
    }

    public void setCopyIds(Set<Long> copyIds) {
        this.copyIds = copyIds;
    }

    public Set<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Set<Long> authorIds) {
        this.authorIds = authorIds;
    }
}
