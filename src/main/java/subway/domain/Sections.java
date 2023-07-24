package subway.domain;

import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

import java.util.*;
import java.util.function.Predicate;

public final class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        validateLine(sections);
        this.sections = sections;
    }

    private static void validateLine(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalLineException("해당 노선은 생성되지 않았습니다.");
        }
    }

    public Optional<Section> findConnectedSection(final Section newSection) {
        final boolean connectWithUpStation = checkConnection(newSection);
        final Optional<Section> connectedSection = findConnectedSection(newSection, connectWithUpStation);

        return connectedSection.flatMap(section ->
                connectedSection(section, newSection, connectWithUpStation)
        );
    }

    private Optional<Section> connectedSection(final Section existSection,
                                               final Section section,
                                               final boolean isUpStation) {
        validateDistance(existSection, section.getDistance());

        if (isUpStation) {
            return Optional.of(existSection.upStationId(section));
        }
        return Optional.of(existSection.downStationId(section));
    }

    private boolean checkConnection(final Section section) {
        final boolean isUpStationExists = checkStationExist(section.getUpStationId());
        final boolean isDownStationExists = checkStationExist(section.getDownStationId());
        validateStations(isUpStationExists, isDownStationExists);
        return isUpStationExists;
    }

    private void validateStations(final boolean isUpStationExists, final boolean isDownStationExists) {
        if (isUpStationExists == isDownStationExists) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
    }

    private Optional<Section> findConnectedSection(final Section section, final boolean isUpStation) {
        if (isUpStation) {
            return connectedSection(section::compareUpStationId);
        }
        return connectedSection(section::compareDownStationId);
    }

    private Optional<Section> connectedSection(final Predicate<Section> compareStationId) {
        return sections.stream()
                .filter(compareStationId)
                .findAny();
    }

    private boolean checkStationExist(final long stationId) {
        return sections.stream()
                .anyMatch(section -> section.containsStation(stationId));
    }

    private void validateDistance(final Section existSection, final int distance) {
        if (!existSection.isDistanceGreaterThan(distance)) {
            throw new IllegalSectionException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
    }
}
