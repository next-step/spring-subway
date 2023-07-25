package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.util.Assert;

class Color {

    private static final int MAX_COLOR_LENGTH = 20;

    final String value;

    Color(String value) {
        Assert.isTrue(value.length() <= MAX_COLOR_LENGTH,
                MessageFormat.format("Color {0}의 사이즈는 {1}보다 클 수 없습니다.", value, MAX_COLOR_LENGTH));
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Color)) {
            return false;
        }
        Color color = (Color) o;
        return Objects.equals(value, color.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Color{" +
                "value='" + value + '\'' +
                '}';
    }
}
