package subway.domain;

import java.util.Objects;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

public class Distance {

    private final int distance;

    public Distance(final int distance) {
        if (distance <= 0) {
            throw new SubwayException(ErrorCode.INVALID_DISTANCE_POSITIVE);
        }
        this.distance = distance;
    }

    public Distance subtract(final Distance other) {
        if (distance <= other.distance) {
            throw new SubwayException(ErrorCode.INVALID_DISTANCE_COMPARE);
        }

        return new Distance(distance - other.distance);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    public Distance add(Distance other) {
        return new Distance(distance + other.distance);
    }
}
