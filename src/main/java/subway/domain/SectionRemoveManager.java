package subway.domain;

import java.util.Optional;

public class SectionRemoveManager {

    private final Sections sections;
    private static final int MIN_SECTION_COUNT = 1;

    public SectionRemoveManager(final Sections sections) {
        this.sections = sections;
    }

    public void validate(final Station station) {
        validateSize();
        validateStationInLine(station);
    }

    public Optional<Section> lookForChange(final Station station) {
        if (sections.isLast(station)) {
            return Optional.empty();
        }

        if (sections.isFirst(station)) {
            return Optional.empty();
        }

        Section upperSection = sections.filter(section -> section.isDownStation(station))
            .orElseThrow(() -> new IllegalStateException());
        Section lowerSection = sections.filter(section -> section.isUpStation(station))
            .orElseThrow(() -> new IllegalStateException());
        return Optional.of(upperSection.extendBy(lowerSection));
    }

    private void validateSize() {
        if (sections.getSize() <= MIN_SECTION_COUNT) {
            throw new IllegalStateException("노선의 구간이 " + MIN_SECTION_COUNT + "개인 경우 삭제할 수 없습니다.");
        }
    }

    private void validateStationInLine(final Station station) {
        if (!sections.hasStation(station)) {
            throw new IllegalArgumentException("노선에 등록되어 있지 않은 역은 제거할 수 없습니다.");
        }
    }
}
