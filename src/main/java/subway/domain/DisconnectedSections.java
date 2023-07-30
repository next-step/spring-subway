package subway.domain;


import subway.exception.SubwayException;

import java.util.List;

public final class DisconnectedSections {

    private static final int SIZE_HAS_UPDATE_SECTION = 2;

    private final Section deleteSection;
    private final Section updateSection;

    private DisconnectedSections(final Section deleteSection, final Section updateSection) {
        this.deleteSection = deleteSection;
        this.updateSection = updateSection;
    }

    public static DisconnectedSections of(final List<Section> sections) {
        validateSections(sections);
        return create(sections);
    }

    private static DisconnectedSections create(final List<Section> sections) {
        Section deleteSection = sections.get(0);
        Section updateSection = Section.NULL;
        if (sections.size() == SIZE_HAS_UPDATE_SECTION) {
            updateSection = sections.get(1);
            updateSection = updateSection.combine(deleteSection);
        }

        return new DisconnectedSections(deleteSection, updateSection);
    }

    private static void validateSections(final List<Section> sections) {
        if (sections.size() > SIZE_HAS_UPDATE_SECTION) {
            throw new IllegalArgumentException("[ERROR] 노선의 구간들이 올바르게 연결되어 있지 않습니다.");
        }
        if (sections.isEmpty()) {
            throw new SubwayException("해당 노선에 삭제할 역이 존재하지 않습니다.");
        }
    }

    public Section getDeleteSection() {
        return deleteSection;
    }

    public Section getUpdateSection() {
        return updateSection;
    }
}
