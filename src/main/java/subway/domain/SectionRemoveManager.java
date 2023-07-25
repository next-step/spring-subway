package subway.domain;

import java.util.Optional;

public class SectionRemoveManager {

    private static final int MIN_SECTION_COUNT = 1;

    private final Sections sections;

    public SectionRemoveManager(final Sections sections) {
        this.sections = sections;
    }

    public void validate(final Station station) {
        validateSize();
        validateStationInLine(station);
    }

    public Optional<Section> lookForChange(final Station station) {
        if (isAtEndOfLine(station)) {
            return Optional.empty();
        }

        Section upperSection = getUpperSection(station);
        Section lowerSection = getLowerSection(station);
        return Optional.of(upperSection.extendBy(lowerSection));
    }

    private boolean isAtEndOfLine(final Station station) {
        return sections.isLast(station) || sections.isFirst(station);
    }

    private Section getUpperSection(final Station station) {
        return sections.filter(section -> section.isDownStation(station))
            .orElseThrow(() -> new IllegalStateException("역의 위 구간을 찾을 수 없습니다."));
    }

    private Section getLowerSection(final Station station) {
        return sections.filter(section -> section.isUpStation(station))
            .orElseThrow(() -> new IllegalStateException("역의 아래 구간을 찾을 수 없습니다."));
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
