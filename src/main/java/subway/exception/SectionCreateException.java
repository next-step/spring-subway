package subway.exception;

public class SectionCreateException extends SubwayBaseException {

    private static final Integer CODE = 400;

    public SectionCreateException(String message) {
        super(message, CODE);
    }

}
