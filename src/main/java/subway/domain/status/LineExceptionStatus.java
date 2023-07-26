package subway.domain.status;

public enum LineExceptionStatus {

    DUPLICATED_SECTIONS("LINE-401"),
    DISCONNECT_FAIL_DELETABLE_SIZE("LINE-402"),
    OVERFLOWED_COLOR_NAME("LINE-403"),
    ;

    private final String status;

    LineExceptionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
