package chervonnaya.dto;

import java.util.Objects;

public class BaseDTO {
    private Long id;

    public BaseDTO() {

    }

    public BaseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDTO baseDTO = (BaseDTO) o;
        return id.equals(baseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
