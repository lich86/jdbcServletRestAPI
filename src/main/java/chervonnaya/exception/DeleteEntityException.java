package chervonnaya.exception;

public class DeleteEntityException extends RuntimeException{
    public DeleteEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
