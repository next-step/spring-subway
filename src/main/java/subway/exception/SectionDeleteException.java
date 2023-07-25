package subway.exception;

public class SectionDeleteException extends SubwayBaseException {

    private static final Integer CODE = 400;

    public SectionDeleteException(String message) {
        super(message, CODE);
    }

}
