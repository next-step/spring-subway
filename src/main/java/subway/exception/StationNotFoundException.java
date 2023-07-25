package subway.exception;

public class StationNotFoundException extends SubwayBaseException {

    private static final Integer CODE = 404;

    private static final String MESSAGE = "해당하는 아이디의 역이 없습니다. 입력값 : ";

    public StationNotFoundException(String id) {
        super(MESSAGE + id, CODE);
    }

    public StationNotFoundException(Long id) {
        this(Long.toString(id));
    }
}
