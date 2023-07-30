package subway.exception;

public enum ErrorCode {

    ONLY_ONE_OVERLAPPED_STATION(400, "SECTION_001","새로운 구간은 기존 구간과 1개 역만 겹쳐야 합니다."),
    AT_LEAST_ONE_SECTION(400, "SECTION_002","최소 1개 이상의 구간이 있어야 합니다."),
    CANNOT_CLOSE_LAST_SECTION(400, "SECTION_003","현재 노선의 마지막 구간은 삭제할 수 없습니다."),
    NOT_EXIST_STATION_IN_LINE(400, "SECTION_004","폐역할 역이 노선에 존재하지 않습니다."),
    NOT_FOUND_STATION_IN_SECTION(500, "SECTION_005","폐역할 역을 찾을 수 없습니다."),
    TWO_MORE_TERMINAL_STATION(500, "SECTION_006","같은 방향의 종점역이 두 개 이상입니다."),
    CANNOT_FIND_TERMINAL_UP_STATION(500, "SECTION_007","상행 종점역을 찾을 수 없습니다."),
    CANNOT_CONSTRUCTED_IN_MIDDLE(500, "SECTION_008","중간에 삽입될 수 없는 구간입니다."),
    SAME_STATION_SECTION(400, "SECTION_009","상행역과 하행역은 다른 역이어야 합니다."),
    NOT_OVERLAPPED_SECTION(500, "SECTION_010","구간이 서로 겹치지 않습니다."),
    NOT_CONNECTED_SECTION(400, "SECTION_011","서로 연결되어 있지 않은 구간을 합칠 수 없습니다."),
    LONGER_THAN_ORIGIN_SECTION(400, "SECTION_012","삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다."),
    NEGATIVE_DISTANCE(400, "DISTANCE_001","거리는 양수여야 합니다."),
    DUPLICATED_LINE_NAME(400, "LINE_001","노선 이름은 중복될 수 없습니다."),
    NULL_LINE_NAME(400, "LINE_002","노선 이름은 null일 수 없습니다."),
    NULL_LINE_COLOR(400, "LINE_003","노선 색은 null일 수 없습니다."),
    LONG_LINE_NAME(400, "LINE_004","노선 이름은 255자 이하여야 합니다."),
    LONG_LINE_COLOR(400, "LINE_005","노선 색은 20자 이하여야 합니다."),
    DUPLICATED_STATION_NAME(400, "STATION_001","역 이름은 중복될 수 없습니다."),
    INVALID_STATION_ID(400, "STATION_002","역 id가 올바르지 않습니다."),
    NULL_STATION_NAME(400, "STATION_003","역 이름은 null일 수 없습니다."),
    LONG_STATION_NAME(400, "STATION_004","역 이름은 255자 이하여야 합니다."),
    SAME_SOURCE_TARGET(400, "PATH_001", "경로 탐색 시 출발역과 도착역은 달라야 합니다."),
    NO_CONNECTED_PATH(400, "PATH_002", "출발역과 도착역이 서로 연결되어 있지 않습니다."),
    NOT_EXIST_IN_DB(400, "DB_001", "해당 데이터를 찾을 수 없습니다."),
    UNKNOWN_DB_ERROR(500, "DB_002", "DB에서 알 수 없는 오류가 발생했습니다."),
    UNKNOWN_SERVER_ERROR(500, "SERVER_001", "서버에서 알 수 없는 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String description;

    ErrorCode(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
