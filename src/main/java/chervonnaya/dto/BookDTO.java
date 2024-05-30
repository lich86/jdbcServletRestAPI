package chervonnaya.dto;

import chervonnaya.model.enums.Language;

import java.util.Set;

public class BookDTO {
    private String originalTitle;
    private Language originalLanguage;
    private String description;
    private Set<Long> copyId;
    private Set<Long> authorIds;

    public BookDTO() {

    }

    public BookDTO(String originalTitle, Language originalLanguage, String description, Set<Long> copyId, Set<Long> authorIds) {
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.description = description;
        this.copyId = copyId;
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

    public Set<Long> getCopyId() {
        return copyId;
    }

    public void setCopyId(Set<Long> copyId) {
        this.copyId = copyId;
    }

    public Set<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Set<Long> authorIds) {
        this.authorIds = authorIds;
    }
}
