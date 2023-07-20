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
        return countByStationIds(upStationId, downStationId) == 2;
    }

    public boolean containsNeither(final Long upStationId, final Long downStationId) {
        return countByStationIds(upStationId, downStationId) == 0;
    }

    public Optional<Section> findLastSection() {
        final Set<Long> upStationIds = toSetWithMapper(Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(Section::getDownStationId);

        final Set<Long> intersectionIds = intersection(upStationIds, downStationIds);
        downStationIds.removeAll(intersectionIds);

        return values.stream().filter(section -> downStationIds.contains(section.getDownStationId()))
                .findAny();
    }

    public boolean isEqualSizeToOne() {
        return this.values.size() == 1;
    }

    public boolean isEndStation(final Long upStationId, final Long downStationId) {
        final Set<Long> upStationsIds = toSetWithMapper(Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(Section::getDownStationId);

        final Set<Long> differenceIds =
                difference(union(upStationsIds, downStationIds), intersection(upStationsIds, downStationIds));

        return differenceIds.contains(upStationId) || differenceIds.contains(downStationId);
    }

    private Set<Long> union(final Set<Long> upStationIds, final Set<Long> downStationIds) {
        final Set<Long> unionIds = new HashSet<>(upStationIds);
        unionIds.addAll(downStationIds);

        return unionIds;
    }

    private Set<Long> intersection(final Set<Long> upStationIds, final Set<Long> downStationIds) {
        final Set<Long> intersectionIds = new HashSet<>(upStationIds);
        intersectionIds.retainAll(downStationIds);

        return intersectionIds;
    }

    private Set<Long> difference(final Set<Long> upStationIds, final Set<Long> downStationIds) {
        final Set<Long> differenceIds = new HashSet<>(upStationIds);
        differenceIds.removeAll(downStationIds);

        return differenceIds;
    }

    private Set<Long> toSetWithMapper(final Function<Section, Long> mapper) {
        return values.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    private int countByStationIds(final Long... targetIds) {
        final Set<Long> stationIds = new HashSet<>();
        for (Section section : values) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        stationIds.retainAll(Arrays.asList(targetIds));

        return stationIds.size();
    }
}
