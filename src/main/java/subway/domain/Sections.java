package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private static final String INVALID_STATIONS_EXCEPTION_MESSAGE = "삽입 시 기준역은 한 개의 역이어야 합니다.";
    private static final String EMPTY_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";
    private static final String AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE = "구간은 0개가 될 수 없습니다.";
    private static final String CANNOT_FIND_START_SECTION_EXCEPTION_MESSAGE = "출발역에 해당되는 구간을 찾을 수 없습니다.";
    private static final String TWO_MORE_START_STATION_EXCEPTION_MESSAGE = "상행 종점역이 두 개 이상입니다.";
    private static final String CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE = "상행 종점역을 찾을 수 없습니다.";
    private static final String LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE = "삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.";
    private static final String NOT_EXIST_STATION_EXCEPTION_MESSAGE = "삭제할 역이 존재하지 않습니다.";

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sorted(sections);
    }

    public List<Section> sectionsForRemoval(final Station station) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(station) || section.isSameDownStation(station))
                .collect(Collectors.toList());
    }

    public void validateDelete(final Station deleteStation) {
        if (this.sections.size() <= 1) {
            throw new IllegalStateException(AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE);
        }

        if (notExistStation(deleteStation)) {
            throw new IllegalArgumentException(NOT_EXIST_STATION_EXCEPTION_MESSAGE);
        }
    }

    private boolean notExistStation(Station deleteStation) {
        return !createUpStations(this.sections).contains(deleteStation)
                && !lastSection().isSameDownStation(deleteStation);
    }

    private List<Section> sorted(final List<Section> sections) {
        if (sections.isEmpty()) {
            return sections;
        }

        Map<Station, Section> stationToSection = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, section -> section));

        return combineUnsortedSections(sections, stationToSection);
    }

    private List<Section> combineUnsortedSections(final List<Section> sections, final Map<Station, Section> stationToSection) {
        List<Section> sortedSection = new ArrayList<>();

        Section nextSection = findByUpStation(sections, findFirstStation(sections));
        sortedSection.add(nextSection);

        for (int i = 1; i < sections.size(); i++) {
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
                .orElseThrow(() -> new IllegalStateException(CANNOT_FIND_START_SECTION_EXCEPTION_MESSAGE));
    }

    private Station findFirstStation(final List<Section> sections) {
        Set<Station> endUpStations = createUpStations(sections);
        endUpStations.removeAll(createDownStations(sections));

        if (endUpStations.size() > 1) {
            throw new IllegalStateException(TWO_MORE_START_STATION_EXCEPTION_MESSAGE);
        }

        return endUpStations.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE));
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

    public Section cut(final Section oldSection, final Section newSection) {
        validateDistance(oldSection, newSection);

        Distance reducedDistance = oldSection.distanceDifference(newSection);

        if (oldSection.isSameUpStation(newSection)) {
            return new Section(newSection.getDownStation(), oldSection.getDownStation(), reducedDistance);
        }

        return new Section(oldSection.getUpStation(), newSection.getUpStation(), reducedDistance);

    }

    public Section oldSection(final Section newSection) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow();
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

    private void validateDistance(final Section oldSection, final Section newSection) {
        if (oldSection.shorterOrEqualTo(newSection)) {
            throw new IllegalArgumentException(LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateEmpty() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
            throw new IllegalArgumentException(INVALID_STATIONS_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
