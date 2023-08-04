package subway.domain;

import java.util.Objects;
import subway.exception.ErrorCode;
import subway.exception.SectionException;

public class Distance {

    private final int value;

    public Distance(final int value) {
        validatePositive(value);

        this.value = value;
    }

    private void validatePositive(final int distance) {
        if (distance <= 0) {
            throw new SectionException(ErrorCode.NOT_POSITIVE_DISTANCE, "거리는 양수여야 합니다.");
        }
    }

    public boolean shorterOrEqualTo(final Distance other) {
        return this.value <= other.value;
    }

    public Distance subtract(final Distance other) {
        return new Distance(this.value - other.value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
