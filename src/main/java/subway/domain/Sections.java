package subway.domain;

import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

import java.util.*;

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

    public Optional<Section> updateForInsert(final Section newSection) {
        final boolean isUpStationExists = validateStations(newSection);

        final Section existSection = findExistSection(newSection, isUpStationExists);

        if (existSection == null) {
            return Optional.empty();
        }

        validateDistance(existSection, newSection.getDistance());
        if (isUpStationExists) {
            return Optional.ofNullable(existSection.upStationId(newSection));
        }
        return Optional.ofNullable(existSection.downStationId(newSection));
    }

    private boolean validateStations(final Section section) {
        final boolean upCheck = checkStationExist(section.getUpStationId());
        final boolean downCheck = checkStationExist(section.getDownStationId());
        if (upCheck == downCheck) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
        return upCheck;
    }

    private Section findExistSection(final Section section, final boolean isUpStation) {
        if (isUpStation) {
            return sections.stream()
                    .filter(section::compareUpStationId)
                    .findAny().orElse(null);
        }
        return sections.stream()
                .filter(section::compareDownStationId)
                .findAny().orElse(null);
    }

    private boolean checkStationExist(final long stationId) {
        return sections.stream()
                .anyMatch(section -> section.containsStation(stationId));
    }

    private void validateDistance(final Section original, final int distance) {
        if (!original.isDistanceGreaterThan(distance)) {
            throw new IllegalSectionException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
    }
}
