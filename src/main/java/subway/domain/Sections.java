package subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import subway.exception.SubwayException;

public class Sections {

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = Collections.unmodifiableList(getSortedSections(values));
    }

    public boolean containsBoth(final Long upStationId, final Long downStationId) {
        return countByStationIds(upStationId, downStationId) == 2;
    }

    public Section getLastSection() {
        if (this.values.size() < 1) {
            throw new SubwayException("노선에 구간이 존재하지 않습니다.");
        }
        return this.values.get(this.values.size() - 1);
    }

    public List<Long> getSortedStationIds() {
        final List<Long> result = new ArrayList<>(List.of(this.values.get(0).getUpStationId()));

        for (Section section : this.values) {
            result.add(section.getDownStationId());
        }

        return result;
    }

    public boolean isEqualSizeToOne() {
        return this.values.size() == 1;
    }

    public boolean isFirstStation(final Long downStationId) {
        final Long firstUpStationId = this.values.get(0).getUpStationId();

        return firstUpStationId.equals(downStationId);
    }

    public boolean isLastStation(final Long upStationId) {
        final Long lastDownStationId = this.values.get(this.values.size() - 1).getDownStationId();

        return lastDownStationId.equals(upStationId);
    }

    public Optional<Section> findContainStationSection(
            final Long upStationId,
            final Long downStationId
    ) {
        return find(this.values, section -> section.containsStations(upStationId, downStationId));
    }

    private Optional<Section> findFirstSectionInNotSortedSections(final List<Section> values) {
        final Set<Long> upStationIds = toSetWithMapper(values, Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(values, Section::getDownStationId);

        upStationIds.removeAll(intersection(upStationIds, downStationIds));

        return find(values, section -> upStationIds.contains(section.getUpStationId()));
    }

    private List<Section> getSortedSections(final List<Section> values) {
        final Map<Long, Section> upStationKeyMap = values.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));

        final Section firstSection = findFirstSectionInNotSortedSections(values).orElseThrow(
                () -> new SubwayException("노선에 구간이 존재하지 않습니다."));

        final List<Section> result = new ArrayList<>(List.of(firstSection));
        Section next = upStationKeyMap.get(firstSection.getDownStationId());

        while (next != null) {
            result.add(next);
            next = upStationKeyMap.get(next.getDownStationId());
        }

        return result;
    }

    private Optional<Section> find(final List<Section> values, final Predicate<Section> predicate) {
        return values.stream()
                .filter(predicate)
                .findAny();
    }

    private Set<Long> intersection(final Set<Long> upStationIds, final Set<Long> downStationIds) {
        final Set<Long> intersectionIds = new HashSet<>(upStationIds);
        intersectionIds.retainAll(downStationIds);

        return intersectionIds;
    }

    private Set<Long> toSetWithMapper(final List<Section> values, final Function<Section, Long> mapper) {
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
