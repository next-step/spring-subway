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
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());

        Set<Station> downStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());

        Station terminal = sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> !upStations.contains(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("하행 종점이 존재하지 않습니다."));

        return station.equals(terminal);
    }

    public boolean contains(final Station station) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList())
                .contains(station);
    }

    public Sections union(final Sections other) {
        return new Sections(
                Stream.of(this.sections, other.sections)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
    }

    public Sections remove(final Station station) {
        validateDownStationTerminal(station);
        validateSize();

        return new Sections(
                sections.stream()
                        .filter(s -> !station.equals(s.getDownStation()))
                        .collect(Collectors.toList())
        );
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
        }
    }

    private void validateDownStationTerminal(final Station station) {
        if (!isTerminal(station)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 지울 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public Sections addSection(Section section) {
        if (contains(section.getUpStation()) && contains(section.getDownStation())) {
            throw new IllegalArgumentException("두 역 모두 기존 노선에 포함될 수 없습니다.");
        }
        if (!contains(section.getUpStation()) && !contains(section.getDownStation())) {
            throw new IllegalArgumentException("두 역 중 하나는 기존 노선에 포함되어야 합니다");
        }
        List<Section> newSection = new ArrayList<>(this.sections);
        newSection.add(section);
        return new Sections(newSection);
        
    }
}
