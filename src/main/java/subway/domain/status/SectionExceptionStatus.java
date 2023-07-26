package subway.domain.status;

public enum SectionExceptionStatus {

    ILLEGAL_DISTANCE("SECTION-401"),
    NULL_STATION("SECTION-402"),
    DUPLICATE_STATION("SECTION-403"),
    NULL_REQUEST_SECTION("SECTION-404"),
    CANNOT_CONNECT_SECTION("SECTION-405"),
    CANNOT_DISCONNECT_SECTION("SECTION-406"),
    ;

    private final String status;

    SectionExceptionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
