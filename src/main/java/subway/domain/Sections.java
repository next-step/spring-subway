package subway.domain;

import java.util.*;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public boolean checkInsertion(final Section section) {
        final boolean upCheck = checkStationExist(section.getUpStationId());
        final boolean downCheck = checkStationExist(section.getDownStationId());
        if(upCheck == downCheck) {
            return false;
        }

        final Section existSection = findExistingSection(section, upCheck);

        if(existSection != null) {
            return checkDistance(existSection, section.getDistance());
        }
        return true;
    }

    private Section findExistingSection(final Section section, final boolean isUpStation) {
        if(isUpStation) {
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

    private boolean checkDistance(final Section original, final int distance) {
        return original.isDistanceGreaterThan(distance);
    }
}
