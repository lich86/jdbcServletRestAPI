package chervonnaya.exception;

public class UpdateEntityException extends RuntimeException{
    public UpdateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
