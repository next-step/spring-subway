package subway.exception;

public enum ErrorCode {
    INVALID_REQUEST(400, "REQ001", "비어 있는 요청 정보가 존재합니다."),

    INVALID_COLOR_LENGTH(400, "COL001", "색상명의 길이를 벗어났습니다."),

    INVALID_STATION_NAME_LENGTH(400, "NAM001", "지하철 역명의 길이가 범위를 벗어났습니다."),
    INVALID_LINE_NAME_LENGTH(400, "NAM001", "노선명의 길이가 범위를 벗어났습니다."),

    NO_SUCH_LINE(404, "LIN001", "존재하지 않는 노선입니다."),
    EXISTS_LINE(400, "LIN002", "이미 존재하는 노선입니다."),

    EMPTY_SECTION(404, "SEC001", "구간이 존재하지 않습니다."),
    INVALID_SECTION(400, "SEC002", "삽입 시 기준역은 한 개의 역이어야 합니다."),
    SAME_SECTION(400, "SEC003", "상행역과 하행역은 다른 역이어야 합니다."),
    AT_LEAST_ONE_SECTION(400, "SEC004", "최소 하나의 구간은 존재해야 합니다."),
    NO_SUCH_SECTION(400, "SEC005", "해당 구간이 존재하지 않습니다."),
    NOT_FOUND_DEPARTURE("SEC006"),
    NOT_FOUND_OLD_SECTION("SEC007"),

    EMPTY_STATION(404, "STA001", "지하철 역이 존재하지 않습니다."),
    NO_SUCH_STATION(404, "STA002", "해당 역이 존재하지 않습니다."),
    TOO_MANY_STATION("STA003"),
    NOT_FOUND_UP_STATION_TERMINAL("STA004"),
    EXISTS_STATION(400, "STA005", "이미 존재하는 지하철역입니다."),

    TOO_LONG_DISTANCE(400, "DST001", "새로운 구간의 거리는 기존 구간의 거리보다 짧아야 합니다."),
    NOT_POSITIVE_DISTANCE(400, "DST002", "거리는 양의 정수여야 합니다."),

    DATABASE_EXISTS(500, "DB_DUPLICATE", "이미 존재하는 데이터입니다."),
    DATABASE_ERROR(500, "DB_UNKNOWN", "데이터베이스에서 알 수 없는 오류가 발생했습니다."),
    UNKNOWN_ERROR("UNKNOWN"),
    ;

    private static final String UNKNOWN_ERROR_MESSAGE = "알 수 없는 오류가 발생했습니다.";

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final String code) {
        this.status = 500;
        this.code = code;
        this.message = UNKNOWN_ERROR_MESSAGE;
    }

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
