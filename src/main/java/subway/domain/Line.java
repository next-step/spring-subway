package subway.domain;

import java.util.Objects;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class Line {

    private final String name;
    private final String color;
    private Long id;

    public Line(final String name, final String color) {
        hasText(name, "이름은 필수입니다.");
        hasText(color, "색깔은 필수입니다.");
        this.name = name;
        this.color = color;
    }

    public Line(final Long id, final String name, final String color) {
        this(name, color);
        notNull(id, "id는 필수입니다.");
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(
                color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
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
