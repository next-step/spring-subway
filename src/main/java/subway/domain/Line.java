package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

public class Line {

    private static final int NAME_LENGTH_LIMIT = 255;
    private static final int COLOR_LENGTH_LIMIT = 20;
    private static final String INPUT_MESSAGE = "입력값: ";

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        validateNotNull(name, color);
        validateNameLength(name);
        validateColorLength(color);

        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateNotNull(String name, String color) {
        validateNameNotNull(name);
        validateColorNotNull(color);
    }

    private void validateNameNotNull(String name) {
        if (name == null) {
            throw new IncorrectRequestException(ErrorCode.NULL_LINE_NAME, INPUT_MESSAGE + name);
        }
    }

    private void validateColorNotNull(String color) {
        if (color == null) {
            throw new IncorrectRequestException(ErrorCode.NULL_LINE_COLOR, INPUT_MESSAGE + color);
        }
    }

    private void validateNameLength(String name) {
        if (name.length() > NAME_LENGTH_LIMIT) {
            throw new IncorrectRequestException(ErrorCode.LONG_LINE_NAME, INPUT_MESSAGE + name);
        }
    }

    private void validateColorLength(String color) {
        if (color.length() > COLOR_LENGTH_LIMIT) {
            throw new IncorrectRequestException(ErrorCode.LONG_LINE_COLOR, INPUT_MESSAGE + color);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
