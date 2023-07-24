package subway.dto;

import subway.domain.Section;

import java.util.List;
import java.util.Optional;

public class SectionRemovalResult {

    private Section sectionToAdd;
    private List<Section> sectionToRemove;

    public SectionRemovalResult() {
    }

    public SectionRemovalResult(Section sectionToAdd, List<Section> sectionToRemove) {
        this.sectionToAdd = sectionToAdd;
        this.sectionToRemove = sectionToRemove;
    }

    public Optional<Section> getSectionToAdd() {
        return Optional.ofNullable(sectionToAdd);
    }

    public List<Section> getSectionToRemove() {
        return sectionToRemove;
    }
}
