package subway.exception;

public abstract class SubwayBaseException extends RuntimeException {

    private final Integer code;

    protected SubwayBaseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
