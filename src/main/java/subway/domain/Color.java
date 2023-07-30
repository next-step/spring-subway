package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;

public class Color {

    private static final int MAX_LENGTH = 20;
    private static final String OUT_OF_LENGTH_EXCEPTION_MESSAGE = "색상명의 길이 제한을 벗어났습니다.";

    private final String value;

    public Color(final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(final String value) {
        if (value == null || value.isBlank() || MAX_LENGTH < value.length()) {
            throw new InvalidRequestException(ErrorCode.INVALID_COLOR_LENGTH, OUT_OF_LENGTH_EXCEPTION_MESSAGE);
        }
    }

    public String getValue() {
        return value;
    }
}
