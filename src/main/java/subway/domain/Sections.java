package subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Sections {

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public boolean containsStation(final Long stationId) {
        return values.stream().anyMatch(section -> section.containsStation(stationId));
    }

    public Optional<Section> findLastPrevSection() {
        return values.stream()
                .filter(Section::isLastPrevSection)
                .findAny();
    }
}
