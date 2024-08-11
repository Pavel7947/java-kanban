package exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String msg, Exception e) {
        super(msg, e);
    }
}
