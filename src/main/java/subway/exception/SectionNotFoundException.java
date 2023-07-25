package subway.exception;

public class SectionNotFoundException extends SubwayBaseException {

    private static final Integer CODE = 404;

    private static final String MESSAGE = "해당하는 아이디의 구간이 없습니다. 입력값 : ";

    public SectionNotFoundException(String id) {
        super(MESSAGE + id, CODE);
    }

    public SectionNotFoundException(Long id) {
        this(Long.toString(id));
    }
}
