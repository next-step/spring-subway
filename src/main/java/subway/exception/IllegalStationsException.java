package subway.exception;

public class IllegalStationsException extends SubwayBadRequestException {

    public IllegalStationsException(final String message) {
        super(message);
    }
}
