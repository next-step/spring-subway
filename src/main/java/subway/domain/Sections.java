package subway.domain;

import java.util.List;
import subway.exception.section.SectionCrossException;
import subway.exception.section.SectionDuplicationStationIdException;
import subway.exception.section.SectionNotConnectingStationException;
import subway.exception.section.SectionRemoveLastStationException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section addSection) {
        if (!sections.isEmpty()) {
            validateSection(addSection);
            validateDuplicationStation(addSection.getDownStation());
        }

        sections.add(addSection);
    }

    private void validateSection(Section addSection) {
        if (getLastSection().isNotSameDownStation(addSection.getUpStation().getId())) {
            validateCrossSection(addSection.getUpStation());
            throw new SectionNotConnectingStationException();
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private void validateCrossSection(Station upStation) {
        boolean result = sections.stream()
            .anyMatch(section -> section.isSameUpStation(upStation.getId()));

        if (result) {
            throw new SectionCrossException();
        }
    }

    private void validateDuplicationStation(Station addDownStation) {
        sections.stream()
            .filter(section -> section.isSameUpStation(addDownStation.getId()))
            .findAny()
            .ifPresent(section -> {throw new SectionDuplicationStationIdException();});
    }

    public void removeLastSection(Long stationId) {
        if (!sections.isEmpty()) {
            validateLastDownStationId(stationId);
            sections.remove(getLastSection());
        }
    }

    private void validateLastDownStationId(Long stationId) {
        if (getLastSection().isNotSameDownStation(stationId)) {
            throw new SectionRemoveLastStationException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
