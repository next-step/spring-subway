package subway.exception;

public class SameSourceAndTargetException extends SubwayBaseException {

    private static final Integer CODE = 400;

    private static final String MESSAGE = "출발역과 도착역은 달라야합니다.";

    public SameSourceAndTargetException() {
        super(MESSAGE, CODE);
    }
}

