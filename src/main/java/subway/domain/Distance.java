package subway.domain;

import java.util.Objects;

public class Distance {

    private static final String DISTANCE_POSITIVE_EXCEPTION_MESSAGE = "거리는 양수여야 합니다.";

    private final int value;

    public Distance(final int value) {
        validatePositive(value);
        this.value = value;
    }

    private void validatePositive(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(DISTANCE_POSITIVE_EXCEPTION_MESSAGE);
        }
    }

    public boolean shorterOrEqualTo(final Distance other) {
        return this.value <= other.value;
    }

    public Distance difference(final Distance other) {
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
