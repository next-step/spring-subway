package subway.domain;

import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

import java.util.*;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        validate(sections);
        this.sections = sections;
    }

    private static void validate(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalLineException("해당 노선은 생성되지 않았습니다.");
        }
    }

    public void checkInsertion(final Section section) {
        final boolean upCheck = checkStationExist(section.getUpStationId());
        final boolean downCheck = checkStationExist(section.getDownStationId());
        if (upCheck == downCheck) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }

        final Section existSection = findExistingSection(section, upCheck);

        if (existSection != null) {
            checkDistance(existSection, section.getDistance());
        }
    }

    private Section findExistingSection(final Section section, final boolean isUpStation) {
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

    private void checkDistance(final Section original, final int distance) {
        if (!original.isDistanceGreaterThan(distance)) {
            throw new IllegalSectionException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
    }
}
