package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;

public class Name {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 255;

    private final String value;

    public Name(final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(final String value) {
        if (value.length() < MIN_LENGTH || MAX_LENGTH < value.length()) {
            throw new InvalidRequestException(ErrorCode.INVALID_NAME_LENGTH, "지하철 역명이나 노선명의 길이 제한을 초과했습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
