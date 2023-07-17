package subway.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    public boolean isTerminal(Station station) {
        Set<Station> upward = sections.stream()
                .map(Section::getUpward)
                .collect(Collectors.toSet());

        Station terminal = sections.stream()
                .map(Section::getDownward)
                .filter(s -> !upward.contains(s))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("하행 종점이 존재하지 않습니다."));

        return station.equals(terminal);
    }

    public boolean contains(Station station) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpward(), section.getDownward()))
                .distinct()
                .collect(Collectors.toList())
                .contains(station);
    }
}
