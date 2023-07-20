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
        return checkOnlyOneStation(section) && checkDistance(section.getDistance());
    }

    private boolean checkOnlyOneStation(final Section section) {
        final boolean upCheck = checkStationExist(section.getUpStationId());
        final boolean downCheck = checkStationExist(section.getDownStationId());
        return upCheck ^ downCheck;
    }

    private boolean checkStationExist(final long stationId) {
        return sections.stream()
                .anyMatch(section -> section.containsStation(stationId));
    }

    private boolean checkDistance(final int distance) {
        return false;
    }
}
