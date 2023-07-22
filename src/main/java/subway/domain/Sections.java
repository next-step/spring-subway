package subway.domain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
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

    public List<Long> getSortedStationIds() {
        final Map<Long, Section> upStationKeyMap = values.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));

        final Section firstSection = findFirstSection().orElseThrow(IllegalArgumentException::new);

        final List<Long> result =
                new ArrayList<>(List.of(firstSection.getUpStationId(), firstSection.getDownStationId()));
        Section next = upStationKeyMap.get(firstSection.getDownStationId());

        while (next != null) {
            result.add(next.getDownStationId());
            next = upStationKeyMap.get(next.getDownStationId());
        }

        return result;
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

    public Optional<Section> findContainStationSection(final Long upStationId, final Long downStationId) {
        return find(section -> section.containsStations(upStationId, downStationId));
    }

    public Optional<Section> findFirstSection() {
        final Set<Long> upStationIds = toSetWithMapper(Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(Section::getDownStationId);

        upStationIds.removeAll(intersection(upStationIds, downStationIds));

        return find(section -> upStationIds.contains(section.getUpStationId()));
    }

    public Optional<Section> findLastSection() {
        final Set<Long> upStationIds = toSetWithMapper(Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(Section::getDownStationId);

        downStationIds.removeAll(intersection(upStationIds, downStationIds));

        return find(section -> downStationIds.contains(section.getDownStationId()));
    }

    private Optional<Section> find(final Predicate<Section> predicate) {
        return values.stream()
                .filter(predicate)
                .findAny();
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

    private int countByStationIds(final Long... targetStationIds) {
        final Set<Long> stationIds = new HashSet<>();
        for (Section section : values) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        stationIds.retainAll(Arrays.asList(targetStationIds));

        return stationIds.size();
    }
}
