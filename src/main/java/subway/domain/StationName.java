package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;

public class StationName {

    private static final int MAX_LENGTH = 255;
    private static final String OUT_OF_RANGE_LENGTH_EXCEPTION_MESSAGE = "지하철 역명의 길이 제한을 초과했습니다.";

    private final String value;

    public StationName(final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(final String value) {
        if (value == null || isInvalidLength(value)) {
            throw new InvalidRequestException(
                    ErrorCode.INVALID_STATION_NAME_LENGTH, OUT_OF_RANGE_LENGTH_EXCEPTION_MESSAGE);
        }
    }

    private boolean isInvalidLength(final String value) {
        return value.isBlank() || MAX_LENGTH < value.length();
    }

    public String getValue() {
        return value;
    }
}