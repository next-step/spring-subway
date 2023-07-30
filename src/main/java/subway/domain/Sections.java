package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

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
                .orElseThrow(() -> new InternalStateException(ErrorCode.CANNOT_FIND_TERMINAL_UP_STATION, ""));
    }

    private Section lastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

    public Section findOverlappedSection(final Section newSection) {
        validateConstructedInMiddle(newSection);
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(ErrorCode.NOT_OVERLAPPED_SECTION, ""));
    }

    private void validateConstructedInMiddle(Section newSection) {
        if (!isConstructedInMiddle(newSection)) {
            throw new InternalStateException(ErrorCode.CANNOT_CONSTRUCTED_IN_MIDDLE, String.format("신설 구간 상행역: %s, 하행역: %s", newSection.getUpStation().getName(), newSection.getDownStation().getName()));
        }
    }

    public Section connectTwoSectionBasedOn(Station station) {
        validateClose(station);
        Section upSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(ErrorCode.NOT_FOUND_STATION_IN_SECTION, station.getName()));

        Section downSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new InternalStateException(ErrorCode.NOT_FOUND_STATION_IN_SECTION, station.getName()));

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
            throw new IncorrectRequestException(ErrorCode.NOT_EXIST_STATION_IN_LINE, station.getName());
        }
    }

    private void validateAtLeastOneSection() {
        if (this.sections.size() <= 1) {
            throw new IncorrectRequestException(ErrorCode.CANNOT_CLOSE_LAST_SECTION, "");
        }
    }

    private void validateNotEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IncorrectRequestException(ErrorCode.AT_LEAST_ONE_SECTION, "");
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
            throw new IncorrectRequestException(ErrorCode.ONLY_ONE_OVERLAPPED_STATION, "");
        }
    }

    private void validateOnlyOneTerminal(Set<Station> terminalStations) {
        if (terminalStations.size() > 1) {
            throw new InternalStateException(
                    ErrorCode.TWO_MORE_TERMINAL_STATION,
                    terminalStations.stream()
                            .map(Station::getName)
                            .collect(Collectors.joining())
            );
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
