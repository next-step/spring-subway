package subway.domain;

import org.springframework.util.ObjectUtils;
import subway.exception.SubwayIllegalArgumentException;

import java.util.Objects;

public class StationName {

    private final String value;

    public StationName(final String value) {
        validateHasValue(value);

        this.value = value;
    }

    private void validateHasValue(final String value) {
        if (ObjectUtils.isEmpty(value)) {
            throw new SubwayIllegalArgumentException("역 이름은 반드시 입력해야합니다. 입력 값: " + value);
        }
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StationName that = (StationName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
