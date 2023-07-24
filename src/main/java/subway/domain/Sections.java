package subway.domain;

import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "새로운 구간은 기존 구간고 1개 역만 겹쳐야 합니다.";
    private static final String EMPTY_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";
    private static final String AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE = "구간은 0개가 될 수 없습니다.";
    private static final String DELETE_ONLY_LAST_SECTION_EXCEPTION_MESSAGE = "마지막 구간만 삭제할 수 있습니다.";
    private static final String TWO_MORE_START_STATION_EXCEPTION_MESSAGE = "상행 종점역이 두 개 이상입니다.";
    private static final String CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE = "상행 종점역을 찾을 수 없습니다.";
    private static final String LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE = "삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.";

    private final List<Section> sections;
    private final Set<Station> upStations;
    private final Set<Station> downStations;

    public Sections(final List<Section> sections) {
        validateEmpty(sections);
        this.upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
        this.downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        this.sections = sorted(sections);
    }

    private List<Section> sorted(final List<Section> sections) {
        Map<Station, Section> stationToSection = new HashMap<>();
        sections.forEach(section -> stationToSection.put(section.getUpStation(), section));
        return sortSections(sections, stationToSection);
    }

    private List<Section> sortSections(final List<Section> sections, final Map<Station, Section> stationToSection) {
        List<Section> sortedSection = new ArrayList<>();
        Section nextSection = stationToSection.get(findFirstStation());
        sortedSection.add(nextSection);
        for (int i = 1; i < sections.size(); i++) {
            Station lastDownStation = nextSection.getDownStation();
            nextSection = stationToSection.get(lastDownStation);
            sortedSection.add(nextSection);
        }
        return sortedSection;
    }

    private Station findFirstStation() {
        Set<Station> endUpStations = new HashSet<>(this.upStations);
        endUpStations.removeAll(downStations);

        validateDuplicatedEndStations(endUpStations);

        return endUpStations.stream()
                .findFirst()
                .orElseThrow(() -> new InternalStateException(CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE));
    }

    private Section lastSection() {
        return this.sections.get(this.sections.size() - 1);
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
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow();
    }

    public List<Station> toStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(lastSection().getDownStation());

        return stations;
    }

    public boolean isInsertedMiddle(final Section newSection) {
        boolean containsUpStation = this.upStations.contains(newSection.getUpStation());
        boolean containsDownStation = this.downStations.contains(newSection.getDownStation());

        return containsUpStation || containsDownStation;
    }

    public void validateInsert(final Section newSection) {
        Set<Station> allStations = new HashSet<>(upStations);
        allStations.addAll(downStations);

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);
    }

    public void validateDelete(final Station lastStation) {
        validateAtLeastOneSection();
        if (!lastSection().getDownStation().equals(lastStation)) {
            throw new IncorrectRequestException(DELETE_ONLY_LAST_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateAtLeastOneSection() {
        if (this.sections.size() <= 1) {
            throw new InternalStateException(AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateDistance(final Section oldSection, final Section newSection) {
        if (oldSection.shorterOrEqualTo(newSection)) {
            throw new IncorrectRequestException(LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IncorrectRequestException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
            throw new IncorrectRequestException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicatedEndStations(Set<Station> endStations) {
        if (endStations.size() > 1) {
            throw new InternalStateException(TWO_MORE_START_STATION_EXCEPTION_MESSAGE);
        }
    }
}
