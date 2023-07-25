package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.util.Assert;

class Name {

    private static final int MAX_NAME_LENGTH = 255;

    final String value;

    Name(String value) {
        Assert.isTrue(value.length() <= MAX_NAME_LENGTH,
                MessageFormat.format("Name {0}의 사이즈는 {1}보다 클 수 없습니다.", value, MAX_NAME_LENGTH));
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Name)) {
            return false;
        }
        Name name = (Name) o;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Name{" +
                "value='" + value + '\'' +
                '}';
    }
}
