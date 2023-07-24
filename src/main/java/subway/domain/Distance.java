package subway.domain;

import java.util.Objects;

public class Distance {
    private final int distance;

    public Distance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 거리는 0보다 커야 합니다.");
        }
        this.distance = distance;
    }

    public Distance subtract(Distance other) {
        if (distance <= other.distance) {
            throw new IllegalArgumentException("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다.");
        }

        return new Distance(distance - other.distance);
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

    public Distance add(Distance other) {
        return new Distance(distance+ other.distance);
    }
}
