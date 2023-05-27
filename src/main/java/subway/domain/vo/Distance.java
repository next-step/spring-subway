package subway.domain.vo;

import java.util.Comparator;
import java.util.Objects;

public class Distance implements Comparator<Distance> {
    private final Double value;

    public Distance(Double distance) {
        validatePositive(distance);
        this.value = distance;
    }

    public Distance(Integer distance) {
        this(Double.valueOf(distance));
    }

    private void validatePositive(Double distance) {
        if (distance < 0) {
            throw new IllegalStateException("거리는 0 이하일 수 없습니다.");
        }
    }

    public static Distance of(Integer distance) {
        return new Distance(distance);
    }

    public Double getDoubleValue() {
        return value;
    }

    public Integer getIntValue() {
        return value.intValue();
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
        return o1.value.compareTo(o2.value);
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }
}
