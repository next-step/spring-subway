package subway.domain;

import java.util.List;
import subway.exception.SectionCrossException;
import subway.exception.SectionDuplicationStationIdException;
import subway.exception.SectionNotConnectingStationException;
import subway.exception.SectionRemoveLastStationException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section addSection) {
        if (!sections.isEmpty()) {
            validateSection(addSection);
            validateDuplicationStation(addSection.getDownStationId());
        }

        sections.add(addSection);
    }

    private void validateSection(Section addSection) {
        if (getLastSection().isNotSameDownStation(addSection.getUpStationId())) {
            validateCrossSection(addSection.getUpStationId());
            throw new SectionNotConnectingStationException();
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private void validateCrossSection(Long upStationId) {
        boolean result = sections.stream()
            .anyMatch(section -> section.isSameUpStation(upStationId));

        if (result) {
            throw new SectionCrossException();
        }
    }

    private void validateDuplicationStation(Long addDownStationId) {
        sections.stream()
            .filter(section -> section.isSameUpStation(addDownStationId))
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
