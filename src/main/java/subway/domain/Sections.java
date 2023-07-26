package subway.domain;

import subway.exception.SubwayException;

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
            throw new SubwayException("두 역 중 하나만 노선에 포함되어야 합니다");
        }
    }

    private void validateNoMatches(final Section section) {
        if (notContains(section.getUpStation()) && notContains(section.getDownStation())) {
            throw new SubwayException("두 역 중 하나는 기존 노선에 포함되어야 합니다");
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
        validateSize();
        validateContainStation(station);
        List<Section> newSections = new ArrayList<>(this.sections);

        Optional<Section> upMatchSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .peek(newSections::remove)
                .findAny();

        Optional<Section> downMatchSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .peek(newSections::remove)
                .findAny();

        if (upMatchSection.isPresent() && downMatchSection.isPresent()) {
            Section upSection = upMatchSection.get();
            Section downSection = downMatchSection.get();
            newSections.add(downSection.union(upSection));
        }

        return new Sections(newSections);
    }

    private void validateContainStation(Station station) {
        if (notContains(station)) {
            throw new SubwayException("노선에 역이 포함되지 않을 때는 삭제할 수 없습니다.");
        }
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new SubwayException("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
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
