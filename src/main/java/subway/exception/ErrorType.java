package subway.exception;

public enum ErrorType {

    SERVICE_ERROR("서비스 동작 중 에러가 발생하였습니다."),
    NOT_EXIST_LINE("존재하지 않는 노선입니다."),
    ALREADY_EXIST_SECTION("이미 등록된 구간입니다."),
    VALIDATE_CONNECT_ABLE_STATION("연결 가능한 구간이 아닙니다."),
    NOT_FOUND_STATION("존재하지 않는 역입니다."),
    VALIDATE_DELETE_SECTION("마지막 역이 아니면 삭제할 수 없습니다.");


    private String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
