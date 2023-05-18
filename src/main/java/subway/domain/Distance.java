package subway.domain;

import subway.exception.ErrorType;
import subway.exception.ServiceException;

public class Distance {

    private static final int MIN_DISTANCE = 0;
    private final int value;

    private Distance(int value) {
        validateDistance(value);
        this.value = value;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new ServiceException(ErrorType.INVALID_DISTANCE);
        }
    }

    public int getValue() {
        return value;
    }
}
