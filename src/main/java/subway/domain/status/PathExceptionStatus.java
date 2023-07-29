package subway.domain.status;

public enum PathExceptionStatus {

    CANNOT_FIND_PATH("PATH-401"),
    ;

    private final String status;

    PathExceptionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
