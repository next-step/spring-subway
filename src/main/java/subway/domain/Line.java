package subway.domain;

import org.springframework.util.Assert;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(final String name, final String color) {
        Assert.hasText(name, "이름은 필수입니다.");
        Assert.hasText(color, "색깔은 필수입니다.");
        this.name = name;
        this.color = color;
    }

    public Line(final Long id, final String name, final String color) {
        this(name, color);
        Assert.notNull(id, "id는 null 일 수 없습니다");
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
