package subway.domain;

import java.util.*;
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
            throw new IllegalArgumentException("두 역 모두 기존 노선에 포함될 수 없습니다.");
        }
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

    private Optional<Section> findMatcingOneSection(final Section section) {
        return sections.stream()
            .filter(section::matchOneStation)
            .findAny();
    }

    public Sections removeStation(final Station station) {
        validateSize();

        List<Section> newSections = new ArrayList<>(this.sections);
        List<Section> removeSection = findRemoveSections(station, newSections);
        removeSection.stream().forEach(newSections::remove);

        if (isMiddleStation(station)) {
            newSections.add(reArrangeSection(station));
        }
        return new Sections(newSections);
    }

    private Section reArrangeSection(final Station station) {
        Optional<Section> upSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny();
        Optional<Section> downSection = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny();
        return downSection.get().add(upSection.get());
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
            if (station.equals(section.getDownStation())) {
                removeSection.add(section);
            }
        }
        return removeSection;
    }

    private void validateSize() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
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
