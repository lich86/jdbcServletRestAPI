package chervonnaya.dto;

import java.util.Set;

public class AuthorDTO extends BaseDTO{
    private String firstName;
    private String lastName;
    private String middleName;
    private String penName;
    private Set<Long> bookIds;

    private Set<String> bookTitles;

    public AuthorDTO() {

    }

    public AuthorDTO(Long authorId, String firstName, String lastName, String middleName, String penName, Set<Long> bookIds, Set<String> bookTitles) {
        super(authorId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.penName = penName;
        this.bookIds = bookIds;
        this.bookTitles = bookTitles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public Set<Long> getBookIds() {
        return bookIds;
    }

    public void setBookIds(Set<Long> bookIds) {
        this.bookIds = bookIds;
    }

    public Set<String> getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(Set<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
}
