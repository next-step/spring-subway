package subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public Sections() {
        this(new ArrayList<>());
    }

    public boolean isTerminal(final Station station) {
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

    public Sections union(Sections other) {
        return new Sections(
                Stream.of(this.sections, other.sections)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
    }

    public List<Section> getSections() {
        return sections;
    }
}
