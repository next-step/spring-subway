package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import subway.domain.status.SectionExceptionStatus;
import subway.util.Assert;

class Distance {

    private static final int MIN_NAME_VALUE = 0;

    final int value;

    Distance(int value) {
        Assert.isTrue(value > MIN_NAME_VALUE,
                () -> MessageFormat.format("distance \"{0}\"는 {1} 이하가 될 수 없습니다.", value, MIN_NAME_VALUE),
                SectionExceptionStatus.ILLEGAL_DISTANCE.getStatus());
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
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
