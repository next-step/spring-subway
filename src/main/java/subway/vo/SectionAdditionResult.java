package subway.vo;

import java.util.List;
import java.util.Optional;
import subway.domain.Section;

public class SectionAdditionResult {

    Section sectionToRemove;
    List<Section> sectionsToAdd;

    public SectionAdditionResult(Section sectionToRemove, List<Section> sectionsToAdd) {
        this.sectionToRemove = sectionToRemove;
        this.sectionsToAdd = sectionsToAdd;
    }

    public Optional<Section> getSectionToRemove() {
        return Optional.ofNullable(sectionToRemove);
    }

    public List<Section> getSectionsToAdd() {
        return sectionsToAdd;
    }
}
