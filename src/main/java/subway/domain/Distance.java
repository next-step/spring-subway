package subway.domain;

import subway.exception.SubwayIllegalArgumentException;

import java.util.Objects;

public class Distance {

    private final int value;

    public Distance(final double value) {
        if (value != Math.floor(value)) {
            throw new SubwayIllegalArgumentException("거리는 정수여야합니다. 입력값: " + value);
        }

        this.value = (int) value;
    }

    public Distance(final int value) {
        validateNotLessThanEqualZero(value);

        this.value = value;
    }

    private void validateNotLessThanEqualZero(final int value) {
        if (value <= 0) {
            throw new SubwayIllegalArgumentException("거리는 0보다 길어야합니다. 입력값: " + value);
        }
    }

    public Distance add(final Distance operand) {
        return new Distance(this.value + operand.value);
    }

    public Distance subtract(final Distance operand) {
        if (isShorterOrEqual(operand)) {
            throw new SubwayIllegalArgumentException(
                    "빼려는 길이가 더 깁니다. 대상 길이: " + this.value + ", 빼려는 길이: " + operand.value
            );
        }

        return new Distance(this.value - operand.value);
    }

    public boolean isShorterOrEqual(final Distance operand) {
        return this.value <= operand.value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
