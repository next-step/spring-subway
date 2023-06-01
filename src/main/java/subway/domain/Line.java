package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId())
                && Objects.equals(getName(), line.getName())
                && Objects.equals(getColor(), line.getColor())
                && Objects.equals(getSections(), line.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), getSections());
    }
}
