package subway.domain;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new Sections());
    }

    public Line(final String name, final String color, final Sections sections) {
        this(null, name, color, sections);
    }

    public Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line addSections(final Section section) {
        return new Line(id, name, color, new Sections(List.of(section)));
    }

    public Line addSection(final Section section) {
        return new Line(name, color, sections.addSection(section));
    }

    public Line removeStation(final Station station) {
        return new Line(id, name, color, sections.removeStation(station));
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

    public Sections getSections() {
        return sections;
    }

}
