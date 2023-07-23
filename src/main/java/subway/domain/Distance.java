package subway.domain;

import java.util.Objects;

public class Distance {

    private static final long MIN_DISTANCE = 1L;

    private long value;

    public Distance(final long distance) {
        validateDistanceValue(distance);
        this.value = distance;
    }

    private void validateDistanceValue(final long distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("거리는 " + MIN_DISTANCE + "이상이어야 합니다.");
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return Objects.equals(value, distance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }
}
