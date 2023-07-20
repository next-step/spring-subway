package subway.vo;

import java.util.List;
import java.util.Optional;
import subway.domain.Section;

public class SectionAdditionResult {

    private final Optional<Section> sectionToRemove;
    private final List<Section> sectionsToAdd;

    public SectionAdditionResult(Optional<Section> sectionToRemove, List<Section> sectionsToAdd) {
        this.sectionToRemove = sectionToRemove;
        this.sectionsToAdd = sectionsToAdd;
    }

    public Optional<Section> getSectionToRemove() {
        return sectionToRemove;
    }

    public List<Section> getSectionsToAdd() {
        return sectionsToAdd;
    }
}
