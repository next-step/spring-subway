package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public Sections addSection(final Section section) {
        validateBothMatches(section);
        validateNoMatches(section);

        List<Section> newSections = new ArrayList<>(this.sections);

        Section oldSection = findTargetSection(section);

        if (oldSection != null) {
            newSections.remove(oldSection);
            newSections.add(oldSection.subtract(section));
        }

        newSections.add(section);
        return new Sections(newSections);
    }

    private void validateBothMatches(final Section section) {
        if (contains(section.getUpStation()) && contains(section.getDownStation())) {
            throw new IllegalArgumentException("두 역 모두 기존 노선에 포함될 수 없습니다.");
        }
    }

    private void validateNoMatches(final Section section) {
        if (notContains(section.getUpStation()) && notContains(section.getDownStation())) {
            throw new IllegalArgumentException("두 역 중 하나는 기존 노선에 포함되어야 합니다");
        }
    }

    private boolean contains(final Station station) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList())
                .contains(station);
    }

    private boolean notContains(final Station station) {
        return !contains(station);
    }

    private Section findTargetSection(final Section section) {
        return sections.stream()
                .filter(section::matchOneStation)
                .findAny()
                .orElse(null);
    }

    public Sections removeStation(final Station station) {
        validateDownStationTerminal(station);
        validateSize();

        return new Sections(
                sections.stream()
                        .filter(s -> !station.equals(s.getDownStation()))
                        .collect(Collectors.toList())
        );
    }

    private void validateDownStationTerminal(final Station station) {
        if (!isTerminalDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 지울 수 없습니다.");
        }
    }

    private boolean isTerminalDownStation(final Station station) {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());

        Station terminal = sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> !upStations.contains(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("하행 종점이 존재하지 않습니다."));

        return station.equals(terminal);
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
        }
    }
    
    public List<Station> getSortedStations() {
        Map<Station, Station> stationMap = new HashMap<>();

        sections.forEach(section -> stationMap.put(section.getUpStation(), section.getDownStation()));

        Set<Station> downStations = new HashSet<>(stationMap.values());

        Station start = sections.stream()
                .map(Section::getUpStation)
                .filter(downStation -> !downStations.contains(downStation))
                .findAny()
                .orElse(null);

        List<Station> sortedStations = new ArrayList<>();

        while (start != null) {
            sortedStations.add(start);
            start = stationMap.get(start);
        }

        return sortedStations;
    }

    public List<Section> getSections() {
        return sections;
    }
}
