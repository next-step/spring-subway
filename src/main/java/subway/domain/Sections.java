package subway.domain;

import static subway.exception.ErrorCode.CAN_NOT_DELETE_WHEN_SECTION_IS_ONE;
import static subway.exception.ErrorCode.INVALID_SECTION_ALREADY_EXISTS;
import static subway.exception.ErrorCode.INVALID_SECTION_NO_EXISTS;
import static subway.exception.ErrorCode.NOT_FOUND_REMOVE_STATION;
import static subway.exception.ErrorCode.NOT_FOUND_START;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.exception.SubwayException;

public class Sections {

    private final List<Section> sections;
    private final Set<Station> stationsCache;

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
        stationsCache = sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections addSection(final Section section) {
        validateNotAlreadyExist(section);

        List<Section> newSections = new ArrayList<>(this.sections);

        findMatchingOneSection(section).ifPresent(oneMatchingSection -> {
            newSections.remove(oneMatchingSection);
            newSections.add(oneMatchingSection.subtract(section));
        });
        newSections.add(section);
        return new Sections(newSections);
    }

    private void validateNotAlreadyExist(final Section section) {
        if (contains(section.getUpStation()) && contains(section.getDownStation())) {
            throw new SubwayException(INVALID_SECTION_ALREADY_EXISTS);
        }
        if (notContains(section.getUpStation()) && notContains(section.getDownStation())) {
            throw new SubwayException(INVALID_SECTION_NO_EXISTS);
        }
    }

    private Optional<Section> findMatchingOneSection(final Section section) {
        return sections.stream()
            .filter(section::matchOneStation)
            .findAny();
    }

    private boolean contains(final Station station) {
        return stationsCache.contains(station);
    }

    private boolean notContains(final Station station) {
        return !contains(station);
    }

    public Sections removeStation(final Station station) {
        validateSize();
        validateContainStation(station);

        List<Section> newSections = new ArrayList<>(this.sections);
        Optional<Section> sectionByUpStation = sections.stream()
            .filter(section -> section.getUpStation().match(station))
            .peek(newSections::remove)
            .findAny();

        Optional<Section> sectionByDownStation = sections.stream()
            .filter(section -> section.getDownStation().match(station))
            .peek(newSections::remove)
            .findAny();

        if (sectionByUpStation.isPresent() && sectionByDownStation.isPresent()) {
            Section mergeSection = sectionByDownStation.get().merge(sectionByUpStation.get());
            newSections.add(mergeSection);
        }
        return new Sections(newSections);
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new SubwayException(CAN_NOT_DELETE_WHEN_SECTION_IS_ONE);
        }
    }

    private void validateContainStation(Station station) {
        if (notContains(station)) {
            throw new SubwayException(NOT_FOUND_REMOVE_STATION);
        }
    }


    public List<Station> getSortedStations() {
        Map<Station, Station> stationMap = new HashMap<>();

        sections.forEach(
            section -> stationMap.put(section.getUpStation(), section.getDownStation()));

        Set<Station> downStations = new HashSet<>(stationMap.values());

        Station start = sections.stream()
            .map(Section::getUpStation)
            .filter(downStation -> !downStations.contains(downStation))
            .findAny()
            .orElseThrow(() -> new SubwayException(NOT_FOUND_START));

        return sortedStation(stationMap, start);
    }

    private List<Station> sortedStation(final Map<Station, Station> stationMap, Station start) {
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

    public Set<Station> getStationsCache() {
        return stationsCache;
    }
}
