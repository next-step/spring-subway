package subway.domain;

import java.util.List;

public class SectionEditResult {

    private final List<Section> addedSections;
    private final List<Section> removedSections;

    public SectionEditResult(final List<Section> addedSections, final List<Section> removedSections) {
        this.addedSections = addedSections;
        this.removedSections = removedSections;
    }

    public List<Section> getAddedSections() {
        return this.addedSections;
    }

    public List<Section> getRemovedSections() {
        return this.removedSections;
    }
}
