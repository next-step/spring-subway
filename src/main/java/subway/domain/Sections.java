package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;
    private final Set<Station> downStationsCache;
    private final Set<Station> upStationsCache;

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
        this.downStationsCache = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        this.upStationsCache = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
    }

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections addSection(final Section section) {
        validateBothMatches(section);
        validateNoMatches(section);

        List<Section> newSections = new ArrayList<>(this.sections);

        findTargetSection(section).ifPresent(oldSection -> {
            newSections.remove(oldSection);
            newSections.add(oldSection.subtract(section));
        });

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
        return downStationsCache.contains(station) || upStationsCache.contains(station);
    }

    private boolean notContains(final Station station) {
        return !contains(station);
    }

    private Optional<Section> findTargetSection(final Section section) {
        return sections.stream()
                .filter(section::matchOneStation)
                .findAny();
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
        Station terminal = findTerminalDownStation();

        return station.equals(terminal);
    }

    private Station findTerminalDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> !upStationsCache.contains(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("하행 종점이 존재하지 않습니다."));
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
        }
    }

    public List<Station> getSortedStations() {
        Map<Station, Station> stationMap = new HashMap<>();

        sections.forEach(section -> stationMap.put(section.getUpStation(), section.getDownStation()));

        Station start = findTerminalUpStation();

        List<Station> sortedStations = new ArrayList<>();

        while (start != null) {
            sortedStations.add(start);
            start = stationMap.get(start);
        }

        return sortedStations;
    }

    private Station findTerminalUpStation() {
        Station start = sections.stream()
                .map(Section::getUpStation)
                .filter(downStation -> !downStationsCache.contains(downStation))
                .findAny()
                .orElse(null);
        return start;
    }

    public List<Section> getSections() {
        return sections;
    }
}
