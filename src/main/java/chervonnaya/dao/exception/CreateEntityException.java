package chervonnaya.dao.exception;

public class CreateEntityException extends RuntimeException{
    public CreateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
