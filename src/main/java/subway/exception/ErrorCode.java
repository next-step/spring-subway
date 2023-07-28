package subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    LINE_ID_NO_EXIST(400, "노선 id가 존재하지 않습니다 : "),
    STATION_ID_NO_EXIST(400, "역 id가 존재하지 않습니다 : "),
    UP_STATION_ID_NO_EXIST(400, "상행역 id가 존재하지 않습니다 : "),
    DOWN_STATION_ID_NO_EXIST(400, "하행역 id가 존재하지 않습니다 : "),
    LINE_NAME_DUPLICATE(400, "노선 이름이 이미 존재합니다 : "),
    STATION_NAME_DUPLICATE(400, "역 이름이 이미 존재합니다 : "),
    STATION_REFERENCED(400, "1개 이상 구간이 참조하는 역은 삭제할 수 없습니다 : "),
    DISTANCE_VALIDATE_POSITIVE(400, "구간의 거리는 0보다 커야 합니다 : "),
    DISTANCE_VALIDATE_SUBTRACT(400, "새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다. 새 구간 거리 : "),
    SECTION_SAME_STATIONS(400, "구간의 상행역과 하행역이 같을 수 없습니다"),
    NEW_SECTION_BOTH_MATCH(400, "두 역 중 하나만 노선에 포함되어야 합니다"),
    NEW_SECTION_NO_MATCH(400, "두 역 중 하나는 기존 노선에 포함되어야 합니다"),
    REMOVE_SECTION_NOT_CONTAIN(400, "노선에 역이 포함되지 않을 때는 삭제할 수 없습니다."),
    SECTION_VALIDATE_SIZE(400, "노선에 구간이 하나일 때는 삭제할 수 없습니다."),
    PATH_SAME_STATIONS(400, "출발역과 도착역이 같은 경우 경로 탐색이 불가능합니다"), PATH_NO_SECTIONS(400, "등록된 구간이 없습니다"),
    STATION_NOT_CONTAINED(400, "해당 역이 포함된 역이 없습니다 : ");
    private final HttpStatus code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = HttpStatus.resolve(code);
        this.message = message;
    }

    public HttpStatus getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
