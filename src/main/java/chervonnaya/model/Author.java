package chervonnaya.model;

import java.util.Objects;
import java.util.Set;

public class Author extends BaseEntity{
    private String firstName;
    private String lastName;
    private String middleName;
    private String penName;
    private Set<Book> books;

    public Author() {
    }

    public Author(Long authorId, String firstName, String lastName, String middleName, String penName, Set<Book> books) {
        super(authorId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.penName = penName;
        this.books = books;
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

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Author author = (Author) o;
        return firstName.equals(author.firstName) && lastName.equals(author.lastName) && Objects.equals(middleName, author.middleName) && Objects.equals(penName, author.penName) && Objects.equals(books, author.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, middleName, penName, books);
    }
}
