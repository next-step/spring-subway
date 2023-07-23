package subway.domain;

import java.util.Set;

public class SectionEditResult {

    private final Set<Section> addedSections;
    private final Set<Section> removedSections;

    public SectionEditResult(final Set<Section> addedSections, final Set<Section> removedSections) {
        this.addedSections = addedSections;
        this.removedSections = removedSections;
    }

    public Set<Section> getAddedSections() {
        return this.addedSections;
    }

    public Set<Section> getRemovedSections() {
        return this.removedSections;
    }
}
