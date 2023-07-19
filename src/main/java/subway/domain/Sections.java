package subway.domain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public boolean containsBoth(final Long upStationId, final Long downStationId) {
        return containsStations(upStationId, downStationId) == 2;
    }

    public boolean containsNeither(final Long upStationId, final Long downStationId) {
        return containsStations(upStationId, downStationId) == 0;
    }

    public Optional<Section> findLastSection() {
        final Set<Long> upStationIds = toSetWithMapper(Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(Section::getDownStationId);

        upStationIds.retainAll(downStationIds);
        downStationIds.removeAll(upStationIds);

        return values.stream()
                .filter(section -> upStationIds.contains(section.getDownStationId()))
                .findAny();
    }

    public boolean isEqualSizeToOne() {
        return this.values.size() == 1;
    }

    private Set<Long> toSetWithMapper(final Function<Section, Long> mapper) {
        return values.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    private int containsStations(final Long... targetIds) {
        final Set<Long> stationIds = new HashSet<>();
        for (Section section : values) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        stationIds.retainAll(Arrays.asList(targetIds));

        return stationIds.size();
    }
}
