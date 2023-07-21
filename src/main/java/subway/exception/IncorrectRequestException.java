package subway.exception;

public class IncorrectRequestException extends IllegalArgumentException{
    public IncorrectRequestException(String errorMessage) {
        super(errorMessage);
    }
}
