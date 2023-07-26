package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.Objects;

public class Distance {
    private final int distance;

    public Distance(final int distance) {
        validatePositive(distance);
        this.distance = distance;
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new SubwayException(ErrorCode.DISTANCE_VALIDATE_POSITIVE, distance);
        }
    }

    public Distance add(Distance other) {
        return new Distance(distance + other.distance);
    }

    public Distance subtract(Distance other) {
        validateSubtract(other);

        return new Distance(distance - other.distance);
    }

    private void validateSubtract(Distance other) {
        if (distance <= other.distance) {
            throw new SubwayException(ErrorCode.DISTANCE_VALIDATE_SUBTRACT, other.distance);
        }
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
