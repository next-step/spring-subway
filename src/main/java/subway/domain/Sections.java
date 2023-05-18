package subway.domain;

import java.util.List;
import subway.exception.SectionRemoveLastStationException;
import subway.exception.SectionDuplicationStationIdException;
import subway.exception.SectionNotConnectingStationException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section addSection) {
        if (!sections.isEmpty()) {
            validateConnectingStation(addSection);
            validateDuplicationStation(addSection.getDownStationId());
        }

        sections.add(addSection);
    }

    private void validateConnectingStation(Section addSection) {
        if (getLastSection().isNotSameDownStation(addSection.getUpStationId())) {
            throw new SectionNotConnectingStationException();
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private void validateDuplicationStation(Long addDownStationId) {
        sections.stream()
            .filter(section -> section.isSameUpStation(addDownStationId))
            .findAny()
            .ifPresent(section -> {throw new SectionDuplicationStationIdException();});
    }

    public void removeLastSection(Long id) {
        if (!sections.isEmpty()) {
            validateLastDownStationId(id);
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
