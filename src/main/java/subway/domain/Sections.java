package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;
    private final Stations stationsCache;

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
        this.stationsCache = Stations.of(sections);
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
            throw new SubwayException(ErrorCode.NEW_SECTION_BOTH_MATCH);
        }
    }

    private void validateNoMatches(final Section section) {
        if (notContains(section.getUpStation()) && notContains(section.getDownStation())) {
            throw new SubwayException(ErrorCode.NEW_SECTION_NO_MATCH);
        }
    }

    private boolean contains(final Station station) {
        return stationsCache.contains(station);
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

    private void validateContainStation(final Station station) {
        if (notContains(station)) {
            throw new SubwayException(ErrorCode.REMOVE_SECTION_NOT_CONTAIN);
        }
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new SubwayException(ErrorCode.SECTION_VALIDATE_SIZE);
        }
    }

    public List<Station> getSortedStations() {
        Map<Station, Station> stationMap = new HashMap<>();
        List<Station> sortedStations = new ArrayList<>();
        Station start = findTerminalUpStation()
                .orElseThrow(() -> new SubwayException(ErrorCode.SECTION_NO_START_STATION));

        sections.forEach(section -> stationMap.put(section.getUpStation(), section.getDownStation()));

        sortedStations.add(start);
        while (stationMap.containsKey(start)) {
            start = stationMap.get(start);
            sortedStations.add(start);
        }

        return sortedStations;
    }

    private Optional<Station> findTerminalUpStation() {
        Stations downStations = new Stations(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet()));

        return stationsCache.subtract(downStations)
                .findAny();
    }

    public List<Section> getSections() {
        return sections;
    }
}
