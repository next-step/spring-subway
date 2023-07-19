package subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public boolean containsStation(final Long stationId) {
        return values.stream().anyMatch(section -> section.containsStation(stationId));
    }

    public Optional<Section> findLastSection() {
        final Set<Long> upStationIds = values.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toSet());

        final Set<Long> downStationIds = values.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toSet());

        upStationIds.retainAll(downStationIds);
        downStationIds.removeAll(upStationIds);

        return values.stream()
                .filter(section -> downStationIds.contains(section.getDownStationId()))
                .findAny();
    }

    public boolean isEqualSizeToOne() {
        return this.values.size() == 1;
    }
}
