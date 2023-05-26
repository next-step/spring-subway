package subway.domain.vo;

import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

@Getter
public class Distance implements Comparator<Distance> {
    private final Integer value;

    public Distance(Integer distance) {
        validatePositive(distance);
        this.value = distance;
    }

    private void validatePositive(Integer distance) {
        if (distance < 0) {
            throw new IllegalStateException("거리는 0 이하일 수 없습니다.");
        }
    }

    public Distance add(Distance distance) {
        return Distance.of(this.value + distance.value);
    }

    public static Distance of(Integer distance) {
        return new Distance(distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return Objects.equals(value, distance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compare(Distance o1, Distance o2) {
        return o1.value - o2.value;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }
}
