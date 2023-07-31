package subway.domain;

import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final LineSections lineSections;

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new LineSections());
    }

    public Line(final String name, final String color, final LineSections lineSections) {
        this(null, name, color, lineSections);
    }

    public Line(final Long id, final String name, final String color, final LineSections lineSections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineSections = lineSections;
    }

    public Line addSection(final Section section) {
        return new Line(name, color, lineSections.addSection(section));
    }

    public Line removeStation(final Station station) {
        return new Line(id, name, color, lineSections.removeStation(station));
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

    public LineSections getSections() {
        return lineSections;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
