package subway.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_DISTANCE_COMPARE(BAD_REQUEST, "새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다."),
    INVALID_DISTANCE_POSITIVE(BAD_REQUEST, "구간의 거리는 0보다 커야 합니다."),
    INVALID_SECTION_ALREADY_EXISTS(BAD_REQUEST, "두 역 모두 기존 노선에 포함될 수 없습니다."),
    INVALID_SECTION_NO_EXISTS(BAD_REQUEST, "두 역 중 하나는 기존 노선에 포함되어야 합니다"),
    CAN_NOT_DELETE_WHEN_SECTION_IS_ONE(BAD_REQUEST, "노선에 구간이 하나일 때는 삭제할 수 없습니다."),
    SAME_UP_AND_DOWN_STATION(BAD_REQUEST, "상행역과 하행역이 같을 수 없습니다"),
    SECTION_DOES_NOT_CONTAIN_SECTION(BAD_REQUEST, "입력으로 들어온 구간이 현재 구간에 포함되지 않습니다"),
    DUPLICATED_STATION_NAME(BAD_REQUEST, "역 이름이 중복됩니다."),
    DUPLICATED_LINE_NAME(BAD_REQUEST, "노선 이름이 중복됩니다."),
    INVALID_LINE_NAME_BLANK(BAD_REQUEST, "라인 이름이 빈칸이면 안됩니다."),
    INVALID_COLOR_NAME_BLANK(BAD_REQUEST, "색깔 이름이 빈칸이면 안됩니다"),
    INVALID_STATION_NAME_BLANK(BAD_REQUEST, "역 이름이 빈칸이면 안됩니다"),
    NOT_FOUND_REMOVE_STATION(NOT_FOUND, "지우려는 역이 존재하지 않습니다"),
    NOT_FOUND_STATION(NOT_FOUND, "찾는 역이 존재하지 않습니다"),
    NOT_FOUND_START(NOT_FOUND, "스타트 지점인 구간이 없습니다."),
    NOT_FOUND_LINE(NOT_FOUND, "찾는 노선이 존재하지 않습니다"),
    NOT_FOUND_SECTION(NOT_FOUND, "찾는 구간이 존재하지 않습니다"),

    NOT_CONNECTED_BETWEEN_START_AND_END_PATH(BAD_REQUEST, "출발점과 도착역이 연결되어 있지 않습니다."),

    SAME_START_END_PATH_POINT(BAD_REQUEST, "출발점과 도착역이 같다면, 길을 생성할 수 없습니다."),
    NOT_FOUND_START_PATH_POINT(NOT_FOUND, "출발역이 존재하지 않습니다."),
    NOT_FOUND_END_PATH_POINT(NOT_FOUND, "도착역이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }


}
