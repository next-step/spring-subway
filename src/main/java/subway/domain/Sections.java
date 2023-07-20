package subway.domain;

import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

import java.util.*;
import java.util.function.Predicate;

public class Sections {

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

    public Optional<Section> updateBeforeInsert(final Section newSection) {
        final boolean isUpStationExists = checkIsUpStationExists(newSection);

        final Section existSection = findExistSection(newSection, isUpStationExists);

        if (existSection == null) {
            return Optional.empty();
        }

        validateDistance(existSection, newSection.getDistance());
        if (isUpStationExists) {
            return Optional.of(existSection.upStationId(newSection));
        }
        return Optional.of(existSection.downStationId(newSection));
    }

    private boolean checkIsUpStationExists(final Section section) {
        final boolean isUpStationExists = checkStationExist(section.getUpStationId());
        final boolean isDownStationExists = checkStationExist(section.getDownStationId());
        validateStations(isUpStationExists, isDownStationExists);
        return isUpStationExists;
    }

    private static void validateStations(final boolean isUpStationExists, final boolean isDownStationExists) {
        if (isUpStationExists == isDownStationExists) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
    }

    private Section findExistSection(final Section section, final boolean isUpStation) {
        if (isUpStation) {
            return existSection(section::compareUpStationId);
        }
        return existSection(section::compareDownStationId);
    }

    private Section existSection(final Predicate<Section> compareStationId) {
        return sections.stream()
                .filter(compareStationId)
                .findAny()
                .orElse(null);
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
