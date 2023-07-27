package subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private final LineName lineName;
    private final Color color;

    public Line(final String lineName, final String color) {
        this(new LineName(lineName), new Color(color));
    }

    public Line(final LineName lineName, final Color color) {
        this.lineName = lineName;
        this.color = color;
    }

    public Line(final Long id, final String lineName, final String color) {
        this(id, new LineName(lineName), new Color(color));
    }

    public Line(final Long id, final LineName lineName, final Color color) {
        this(lineName, color);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LineName getLineName() {
        return lineName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        if (id != null && line.id != null) return Objects.equals(id, line.id);
        return Objects.equals(lineName, line.lineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineName);
    }
}
