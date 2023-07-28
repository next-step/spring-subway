package subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SectionsChange {
    private final List<Section> deletes;
    private final List<Section> inserts;

    public SectionsChange(final List<Section> deletes, final List<Section> inserts) {
        this.deletes = Collections.unmodifiableList(deletes);
        this.inserts = Collections.unmodifiableList(inserts);
    }

    private static SectionsChange of(final Sections previous, final Sections next) {
        Set<Section> previousSet = new HashSet<>(previous.getSections());
        Set<Section> nextSet = new HashSet<>(next.getSections());

        next.getSections().forEach(previousSet::remove);
        previous.getSections().forEach(nextSet::remove);

        return new SectionsChange(List.copyOf(previousSet), List.copyOf(nextSet));
    }

    public static SectionsChange of(final Line previous, final Line next) {
        return of(previous.getSections(), next.getSections());
    }

    public List<Section> getDeletes() {
        return deletes;
    }

    public List<Section> getInserts() {
        return inserts;
    }

}
