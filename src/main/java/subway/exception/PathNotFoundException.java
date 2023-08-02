package subway.exception;

public class PathNotFoundException extends SubwayBaseException {

    private static final Integer CODE = 404;

    private static final String MESSAGE = "두 역사이의 연결된 경로가 없습니다. 출발역 아이디 : %d, 도착역 아이디 : %d";

    public PathNotFoundException(Long source, Long target) {
        super(String.format(MESSAGE, source, target), CODE);
    }
}
