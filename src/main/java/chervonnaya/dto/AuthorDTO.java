package chervonnaya.dto;

import java.util.Set;

public class AuthorDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private String penName;
    private Set<String> bookTitles;

    public AuthorDTO() {

    }

    public AuthorDTO(String firstName, String lastName, String middleName, String penName, Set<String> bookTitles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.penName = penName;
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

    public Set<String> getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(Set<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
}
