package subway.domain;

import subway.exception.SubwayException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Sections {

    private static final int MIN_SIZE_CAN_DELETE = 2;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        validateLine(sections);
        this.sections = sort(sections);
    }

    private List<Section> sort(final List<Section> sections) {
        final Map<Station, Section> upStationWithSection = convert(sections);
        final Section startSection = findStartSectionByStation(sections, findStartStation(upStationWithSection));
        return connectInOrder(upStationWithSection, startSection);
    }

    private Map<Station, Section> convert(final List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Section::getUpStation,
                        section -> section)
                );
    }

    private Section findStartSectionByStation(final List<Section> sections, final Station startStation) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(startStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 노선의 시작 역이 올바르지 않습니다."));
    }

    private Station findStartStation(final Map<Station, Section> upToDownStations) {
        final Set<Station> upStations = new HashSet<>(upToDownStations.keySet());
        final Set<Station> downStations = collectDownStations(upToDownStations);

        upStations.removeAll(downStations);
        return upStations.stream()
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 역들이 제대로 연결되지 않았습니다."));
    }

    private static Set<Station> collectDownStations(final Map<Station, Section> upToDownStations) {
        return upToDownStations.values()
                .stream()
                .map(Section::getDownStation)
                .collect(Collectors.toUnmodifiableSet());
    }

    private List<Section> connectInOrder(final Map<Station, Section> upToDownStations, final Section startStation) {
        final List<Section> sections = new ArrayList<>();
        Section currentSection = startStation;
        while(currentSection != null) {
            sections.add(currentSection);
            currentSection = upToDownStations.get(currentSection.getDownStation());
        }
        return sections;
    }

    public Optional<Section> findConnectedSection(final Section newSection) {
        final boolean connectWithUpStation = checkConnection(newSection);
        final Optional<Section> connectedSection = findConnectedSection(newSection, connectWithUpStation);

        return connectedSection.flatMap(section ->
                connectedSection(section, newSection, connectWithUpStation)
        );
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        sections.forEach(section -> stations.add(section.getDownStation()));
        return Collections.unmodifiableList(stations);
    }

    private void validateLine(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SubwayException("해당 노선은 생성되지 않았습니다.");
        }
    }

    public List<Section> disconnect(final long stationId) {
        validateDisconnection();
        return findDisconnectedSections(stationId);
    }

    private Optional<Section> connectedSection(final Section existSection,
                                               final Section section,
                                               final boolean isUpStation) {
        validateDistance(existSection, section.getDistance());

        if (isUpStation) {
            return Optional.of(existSection.updateUpStation(section));
        }
        return Optional.of(existSection.updateDownStation(section));
    }

    private void validateDisconnection() {
        if (sections.size() < MIN_SIZE_CAN_DELETE) {
            throw new SubwayException("노선에 구간이 최소 2개가 있어야 삭제가 가능합니다.");
        }
    }

    private List<Section> findDisconnectedSections(final long stationId) {
        return sections.stream()
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean checkConnection(final Section section) {
        final boolean isUpStationExists = checkStationExist(section.getUpStation());
        final boolean isDownStationExists = checkStationExist(section.getDownStation());
        validateStations(isUpStationExists, isDownStationExists);
        return isUpStationExists;
    }

    private void validateStations(final boolean isUpStationExists, final boolean isDownStationExists) {
        if (isUpStationExists == isDownStationExists) {
            throw new SubwayException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
    }

    private Optional<Section> findConnectedSection(final Section section, final boolean isUpStation) {
        if (isUpStation) {
            return connectedSection(section::compareUpStationId);
        }
        return connectedSection(section::equalsDownStation);
    }

    private Optional<Section> connectedSection(final Predicate<Section> compareStationId) {
        return sections.stream()
                .filter(compareStationId)
                .findAny();
    }

    private boolean checkStationExist(final Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    private void validateDistance(final Section existSection, final int distance) {
        if (!existSection.isDistanceGreaterThan(distance)) {
            throw new SubwayException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
