package subway.vo;

import java.util.List;
import subway.domain.Section;

public class SectionAdditionResult {

    List<Section> sectionToRemove;
    List<Section> sectionsToAdd;

    public SectionAdditionResult(List<Section> sectionToRemove, List<Section> sectionsToAdd) {
        this.sectionToRemove = sectionToRemove;
        this.sectionsToAdd = sectionsToAdd;
    }

    public List<Section> getSectionToRemove() {
        return sectionToRemove;
    }

    public List<Section> getSectionsToAdd() {
        return sectionsToAdd;
    }
}
