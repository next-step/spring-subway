package subway.domain;

import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final String SHOULD_ONLY_ONE_OVERLAP_EXCEPTION_MESSAGE = "새로운 구간은 기존 구간과 1개 역만 겹쳐야 합니다.";
    private static final String AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";
    private static final String CANNOT_CLOSE_LAST_SECTION_EXCEPTION_MESSAGE = "현재 노선의 마지막 구간은 삭제할 수 없습니다.";
    private static final String CLOSE_NOT_EXIST_STATION_EXCEPTION_MESSAGE = "폐역할 역이 노선에 존재하지 않습니다.";
    private static final String TWO_MORE_TERMINAL_STATION_EXCEPTION_MESSAGE = "같은 방향의 종점역이 두 개 이상입니다.";
    private static final String CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE = "상행 종점역을 찾을 수 없습니다.";
    private static final String NOT_OVERLAPPED_SECTION_EXCEPTION_MESSAGE = "구간이 겹치지 않아 중간에 삽입할 수 없습니다.";
    private static final String CANNOT_CONSTRUCTED_IN_MIDDLE_EXCEPTION_MESSAGE = "중간에 삽입될 수 없는 구간입니다.";

    private final List<Section> sections;
    private final Set<Station> upStations;
    private final Set<Station> downStations;

    public Sections(final List<Section> sections) {
        validateNotEmpty(sections);
        this.upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
        this.downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        this.sections = connectInOrder(sections);
    }

    private List<Section> connectInOrder(final List<Section> sections) {
        Map<Station, Section> sectionsByUpStation = new HashMap<>();
        sections.forEach(section -> sectionsByUpStation.put(section.getUpStation(), section));
        return connectInOrder(sections, sectionsByUpStation);
    }

    private List<Section> connectInOrder(final List<Section> sections, final Map<Station, Section> sectionsByUpStation) {
        List<Section> connectedSection = new ArrayList<>();
        Section nextSection = sectionsByUpStation.get(firstStation());
        connectedSection.add(nextSection);
        for (int i = 1; i < sections.size(); i++) {
            Station lastDownStation = nextSection.getDownStation();
            nextSection = sectionsByUpStation.get(lastDownStation);
            connectedSection.add(nextSection);
        }
        return Collections.unmodifiableList(connectedSection);
    }

    private Station firstStation() {
        Set<Station> terminalUpStation = new HashSet<>(this.upStations);
        terminalUpStation.removeAll(downStations);

        validateOnlyOneTerminal(terminalUpStation);

        return terminalUpStation.stream()
                .findFirst()
                .orElseThrow(() -> new InternalStateException(CANNOT_FIND_START_STATION_EXCEPTION_MESSAGE));
    }

    private Section lastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

    public Section findOverlappedSection(final Section newSection) {
        validateConstructedInMiddle(newSection);
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(NOT_OVERLAPPED_SECTION_EXCEPTION_MESSAGE));
    }

    private void validateConstructedInMiddle(Section newSection) {
        if (!isConstructedInMiddle(newSection)) {
            throw new InternalStateException(CANNOT_CONSTRUCTED_IN_MIDDLE_EXCEPTION_MESSAGE);
        }
    }

    public Section connectTwoSectionBasedOn(Station station) {
        validateClose(station);
        Section upSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(CLOSE_NOT_EXIST_STATION_EXCEPTION_MESSAGE));

        Section downSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(CLOSE_NOT_EXIST_STATION_EXCEPTION_MESSAGE));

        return upSection.connectWith(downSection);
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

    public boolean isConstructedInMiddle(final Section newSection) {
        boolean containsUpStation = this.upStations.contains(newSection.getUpStation());
        boolean containsDownStation = this.downStations.contains(newSection.getDownStation());

        return containsUpStation || containsDownStation;
    }

    public boolean isNotTerminal(Station station) {
        Set<Station> middleStations = new HashSet<>(upStations);
        middleStations.retainAll(downStations);
        return middleStations.contains(station);
    }

    public void validateConstruction(final Section newSection) {
        Set<Station> allStations = gatherAllStations();

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);
    }

    public void validateClose(final Station station) {
        validateAtLeastOneSection();

        Set<Station> allStations = gatherAllStations();
        if (!allStations.contains(station)) {
            throw new IncorrectRequestException(CLOSE_NOT_EXIST_STATION_EXCEPTION_MESSAGE);
        }
    }

    private void validateAtLeastOneSection() {
        if (this.sections.size() <= 1) {
            throw new IncorrectRequestException(CANNOT_CLOSE_LAST_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateNotEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IncorrectRequestException(AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
            throw new IncorrectRequestException(SHOULD_ONLY_ONE_OVERLAP_EXCEPTION_MESSAGE);
        }
    }

    private void validateOnlyOneTerminal(Set<Station> terminalStations) {
        if (terminalStations.size() > 1) {
            throw new InternalStateException(TWO_MORE_TERMINAL_STATION_EXCEPTION_MESSAGE);
        }
    }
}
