package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import subway.exception.ErrorCode;
import subway.exception.SectionException;
import subway.exception.StationException;

public class Sections {

    private static final int MIN_SECTIONS_COUNT = 2;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sorted(sections);
    }

    public List<Section> findSectionsIncluding(final Station station) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(station) || section.isSameDownStation(station))
                .collect(Collectors.toUnmodifiableList());
    }

    private List<Section> sorted(final List<Section> sections) {
        if (sections.isEmpty()) {
            return sections;
        }

        Map<Station, Section> stationToSection = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, section -> section));

        return combineUnsortedSections(sections, stationToSection);
    }

    private List<Section> combineUnsortedSections(final List<Section> sections,
            final Map<Station, Section> stationToSection) {
        List<Section> sortedSection = new ArrayList<>();

        Section nextSection = findByUpStation(sections, firstStation(sections));
        sortedSection.add(nextSection);

        while (sortedSection.size() < sections.size()) {
            Station lastDownStation = nextSection.getDownStation();
            nextSection = stationToSection.get(lastDownStation);
            sortedSection.add(nextSection);
        }

        return sortedSection;
    }

    private Section findByUpStation(final List<Section> sections, final Station upStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NOT_FOUND_DEPARTURE,
                        "출발역에 해당되는 구간을 찾을 수 없습니다."));
    }

    private Station firstStation(final List<Section> sections) {
        Set<Station> endUpStations = createUpStations(sections);
        endUpStations.removeAll(createDownStations(sections));

        if (endUpStations.size() > 1) {
            throw new StationException(ErrorCode.TOO_MANY_STATION, "상행 종점역이 두 개 이상입니다.");
        }

        return endUpStations.stream()
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NOT_FOUND_UP_STATION_TERMINAL,
                        "상행 종점역을 찾을 수 없습니다."));
    }

    private Section lastSection() {
        validateEmpty();

        return this.sections.get(this.sections.size() - 1);
    }

    public boolean isInsertedMiddle(final Section newSection) {
        boolean containsUpStation = createUpStations(this.sections).contains(newSection.getUpStation());
        boolean containsDownStation = createDownStations(this.sections).contains(newSection.getDownStation());

        return containsUpStation || containsDownStation;
    }

    public Section oldSection(final Section newSection) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NOT_FOUND_OLD_SECTION, "기존 구간을 찾을 수 없습니다."));
    }

    private Set<Station> createUpStations(final List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
    }

    private Set<Station> createDownStations(final List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
    }

    public Section connectTerminals() {
        validateEmpty();

        final Station upTerminal = sections.get(0).getUpStation();
        final Station downTerminal = lastSection().getDownStation();
        final int distance = totalDistance();

        return new Section(upTerminal, downTerminal, distance);
    }

    private int totalDistance() {
        return sections.stream()
                .mapToInt(section -> section.getDistance().getValue())
                .sum();
    }

    public List<Station> toStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(lastSection().getDownStation());

        return stations;
    }

    public void validateInsert(final Section newSection) {
        Set<Station> allStations = createUpStations(this.sections);
        allStations.addAll(createDownStations(this.sections));

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);
    }

    public void validateDelete() {
        if (sections.size() < MIN_SECTIONS_COUNT) {
            throw new SectionException(ErrorCode.AT_LEAST_ONE_SECTION, "구간은 0개가 될 수 없습니다.");
        }
    }

    private void validateEmpty() {
        if (this.sections.isEmpty()) {
            throw new SectionException(ErrorCode.EMPTY_SECTION, "최소 1개 이상의 구간이 있어야 합니다.");
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
            throw new SectionException(ErrorCode.INVALID_SECTION, "삽입 시 기준역은 한 개의 역이어야 합니다.");
        }
    }

    public List<Long> getIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean hasSize(final int size) {
        return sections.size() == size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
