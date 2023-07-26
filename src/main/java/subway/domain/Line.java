package subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        if (id != null && line.id != null) return Objects.equals(id, line.id);
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
