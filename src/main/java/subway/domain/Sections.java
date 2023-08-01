package subway.domain;

import subway.exception.SubwayException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static subway.exception.ErrorCode.*;

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

        List<Section> originalSections = new ArrayList<>(sections);

        removeSectionsByStation(station, originalSections);
        mergeSection(station, originalSections);

        return new Sections(originalSections);
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new SubwayException(CAN_NOT_DELETE_WHEN_SECTION_IS_ONE);
        }
    }

    private void validateContainStation(final Station station) {
        if (notContains(station)) {
            throw new SubwayException(NOT_FOUND_REMOVE_STATION);
        }
    }

    private void removeSectionsByStation(final Station station, final List<Section> originalSections) {
        List<Section> matchingSection = sections.stream()
            .filter(section -> section.matchOneStation(station))
            .collect(Collectors.toList());
        originalSections.removeAll(matchingSection);
    }

    private void mergeSection(final Station station, final List<Section> originalSections) {
        Optional<Section> sectionByUpStation = sections.stream()
            .filter(section -> section.getUpStation().match(station))
            .findAny();
        Optional<Section> sectionByDownStation = sections.stream()
            .filter(section -> section.getDownStation().match(station))
            .findAny();
        if (sectionByUpStation.isPresent() && sectionByDownStation.isPresent()) {
            Section mergeSection = sectionByDownStation.get().merge(sectionByUpStation.get());
            originalSections.add(mergeSection);
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
