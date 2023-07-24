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
        Map<Station, Section> nextSectionMap = new HashMap<>();
        sections.forEach(section -> nextSectionMap.put(section.getUpStation(), section));
        return sortSections(sections, nextSectionMap);
    }

    private List<Section> sortSections(final List<Section> sections, final Map<Station, Section> nextSectionMap) {
        List<Section> sortedSection = new ArrayList<>();
        Section nextSection = nextSectionMap.get(findFirstStation());
        sortedSection.add(nextSection);
        for (int i = 1; i < sections.size(); i++) {
            Station lastDownStation = nextSection.getDownStation();
            nextSection = nextSectionMap.get(lastDownStation);
            sortedSection.add(nextSection);
        }
        return Collections.unmodifiableList(sortedSection);
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

    public Section findOverlappedSection(final Section newSection) {
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

    private Set<Station> gatherAllStations() {
        Set<Station> allStations = new HashSet<>(upStations);
        allStations.addAll(downStations);
        return allStations;
    }

    public boolean isInsertedMiddle(final Section newSection) {
        boolean containsUpStation = this.upStations.contains(newSection.getUpStation());
        boolean containsDownStation = this.downStations.contains(newSection.getDownStation());

        return containsUpStation || containsDownStation;
    }

    public void validateInsert(final Section newSection) {
        Set<Station> allStations = gatherAllStations();

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);
    }

    public void validateDelete(final Station lastStation) {
        validateAtLeastOneSection();

        Set<Station> allStations = gatherAllStations();
        if (!allStations.contains(lastStation)) {
            throw new IncorrectRequestException(DELETE_ONLY_LAST_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateAtLeastOneSection() {
        if (this.sections.size() <= 1) {
            throw new IncorrectRequestException(AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE);
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
