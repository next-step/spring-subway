package subway.domain;

public class Color {

    private static final int MAX_LENGTH = 20;

    private final String value;

    public Color(final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(final String value) {
        if (value == null || value.isBlank() || MAX_LENGTH < value.length()) {
            throw new IllegalArgumentException("색상명의 길이 제한을 벗어났습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
