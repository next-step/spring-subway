package subway.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SectionsChange {
    private final List<Section> deletes;
    private final List<Section> inserts;

    public SectionsChange(List<Section> deletes, List<Section> inserts) {
        this.deletes = deletes;
        this.inserts = inserts;
    }

    public static SectionsChange of(Sections previous, Sections next) {
        Set<Section> previousSet = new HashSet<>(previous.getSections());
        Set<Section> nextSet = new HashSet<>(next.getSections());

        next.getSections().forEach(previousSet::remove);
        previous.getSections().forEach(nextSet::remove);

        return new SectionsChange(List.copyOf(previousSet), List.copyOf(nextSet));
    }

    public List<Section> getDeletes() {
        return deletes;
    }

    public List<Section> getInserts() {
        return inserts;
    }

}
