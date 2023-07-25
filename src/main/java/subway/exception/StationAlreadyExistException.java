package subway.exception;

public class StationAlreadyExistException extends SubwayBaseException {

    private static final Integer CODE = 400;

    private static final String MESSAGE = "해당 이름의 역이 이미 존재합니다. 입력값 : ";

    public StationAlreadyExistException(String name) {
        super(MESSAGE + name, CODE);
    }
}
