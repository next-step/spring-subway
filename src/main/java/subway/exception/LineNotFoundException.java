package subway.exception;

public class LineNotFoundException extends SubwayBaseException {

    private static final Integer CODE = 404;

    private static final String MESSAGE = "해당하는 아이디의 노선이 없습니다. 입력값 : ";

    public LineNotFoundException(String id) {
        super(MESSAGE + id, CODE);
    }

    public LineNotFoundException(Long id) {
        this(Long.toString(id));
    }
}
