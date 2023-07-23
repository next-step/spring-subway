package subway.domain;

import static org.springframework.util.StringUtils.*;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateLine(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateLine(String name, String color) {
        if (!hasText(name) || !hasText(color)) {
            throw new IllegalArgumentException("이름, 색상은 반드시 입력해야합니다.");
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

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", color='" + color + '\'' +
            '}';
    }
}
