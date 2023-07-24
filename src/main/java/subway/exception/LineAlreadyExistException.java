package subway.exception;

public class LineAlreadyExistException extends SubwayBaseException {

    private static final Integer CODE = 400;

    private static final String MESSAGE = "해당 이름의 노선이 이미 존재합니다. 입력값 : ";

    public LineAlreadyExistException(String name) {
        super(MESSAGE + name, CODE);
    }
}
