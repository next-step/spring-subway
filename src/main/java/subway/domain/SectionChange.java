package subway.domain;

import java.util.ArrayList;
import java.util.List;

public class SectionChange {

    private final List<Section> sectionsToRemove;
    private final List<Section> sectionsToAdd;

    public SectionChange() {
        sectionsToRemove = new ArrayList<>();
        sectionsToAdd = new ArrayList<>();
    }

    public void appendSectionToRemove(Section section) {
        sectionsToRemove.add(section);
    }

    public void appendSectionToAdd(Section section) {
        sectionsToAdd.add(section);
    }

    public void appendSectionsToRemove(List<Section> sections) {
        sectionsToRemove.addAll(sections);
    }

    public void appendSectionsToAdd(List<Section> sections) {
        sectionsToAdd.addAll(sections);
    }

    public List<Section> getSectionsToRemove() {
        return sectionsToRemove;
    }

    public List<Section> getSectionsToAdd() {
        return sectionsToAdd;
    }
}
