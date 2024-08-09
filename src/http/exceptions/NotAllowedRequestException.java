package http.exceptions;

public class NotAllowedRequestException extends RuntimeException {
    public NotAllowedRequestException(String message) {
        super(message);
    }
}
