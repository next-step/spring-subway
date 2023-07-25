package subway.domain;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

public class Sections {

    private final List<Section> sections;
    private final Set<Station> cache;

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
        cache = sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections addSection(final Section section) {
        validateNotAlreadyExist(section);

        List<Section> newSections = new ArrayList<>(this.sections);

        Optional<Section> oneMatcingStation = findMatcingOneSection(section);
        if (oneMatcingStation.isPresent()) {
            newSections.remove(oneMatcingStation);
            newSections.add(oneMatcingStation.get().subtract(section));
        }

        newSections.add(section);
        return new Sections(newSections);
    }

    private void validateNotAlreadyExist(final Section section) {
        if (contains(section.getUpStation()) && contains(section.getDownStation())) {
            throw new SubwayException(ErrorCode.INVALID_SECTION_ALREADY_EXISTS);
        }
        if (notContains(section.getUpStation()) && notContains(section.getDownStation())) {
            throw new SubwayException(ErrorCode.INVALID_SECTION_NO_EXISTS);
        }
    }

    private boolean contains(final Station station) {
        return cache.contains(station);
    }

    private boolean notContains(final Station station) {
        return !contains(station);
    }

    private Optional<Section> findMatcingOneSection(final Section section) {
        return sections.stream()
            .filter(section::matchOneStation)
            .findAny();
    }

    public Sections removeStation(final Station station) {
        validateSize();
        validateContainStation(station);
        List<Section> newSections = new ArrayList<>(this.sections);
        List<Section> removeSection = findRemoveSections(station, newSections);

        newSections.removeAll(removeSection);

        if (isMiddleStation(station)) {
            newSections.add(reArrangeSection(station));
        }
        return new Sections(newSections);
    }

    private void validateContainStation(Station station) {
        if (notContains(station)) {
            throw new SubwayException(ErrorCode.NOT_FOUND_REMOVE_STATION);
        }
    }


    private Section reArrangeSection(final Station station) {
        Section upSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny()
            .orElseThrow(() -> new IllegalStateException(""));
        Section downSection = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny()
            .orElseThrow(() -> new IllegalStateException(""));
        return downSection.add(upSection);
    }

    private boolean isMiddleStation(final Station station) {
        return sections.stream()
            .filter(s -> s.matchOneStation(station))
            .count() > 1;
    }

    private List<Section> findRemoveSections(
        final Station station,
        final List<Section> newSections
    ) {
        List<Section> removeSection = new ArrayList<>();
        for (Section section : newSections) {
            if (section.matchOneStation(station)) {
                removeSection.add(section);
            }
        }
        return removeSection;
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new SubwayException(ErrorCode.CAN_NOT_DELETE_WHEN_SECTION_IS_ONE);
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
            .orElseThrow(() -> new IllegalStateException("스타트 지점인 구간이 없습니다."));

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
}
