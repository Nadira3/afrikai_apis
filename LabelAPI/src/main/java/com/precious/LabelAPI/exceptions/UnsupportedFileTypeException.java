pacakge com.precious.LabelAPI.exceptions;

// Exception for unsupported file types
public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
