package subway.domain;

import java.util.ArrayList;
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
    private final List<Long> stationIds;

    public Sections(final List<Section> values) {
        this.values = Collections.unmodifiableList(getSortedSections(values));
        this.stationIds = sortedStationIds();
    }

    public boolean containsBoth(final Long upStationId, final Long downStationId) {
        return containsStation(upStationId) && containsStation(downStationId);
    }

    public boolean containsStation(final Long stationId) {
        return this.stationIds.contains(stationId);
    }

    public Section getFirstSection() {
        if (this.values.size() < 1) {
            throw new SubwayException("노선에 구간이 존재하지 않습니다.");
        }
        return this.values.get(0);
    }

    public Section getLastSection() {
        if (this.values.size() < 1) {
            throw new SubwayException("노선에 구간이 존재하지 않습니다.");
        }
        return this.values.get(this.values.size() - 1);
    }

    public Section getBetweenSectionToNext(final Long stationId) {
        return find(this.values, section -> section.isSameDownStationId(stationId))
                .orElseThrow(() -> new SubwayException("해당 역을 하행역으로 가지는 구간이 존재하지 않습니다. 역 ID : " + stationId));
    }

    public Section getBetweenSectionToPrev(final Long stationId) {
        return find(this.values, section -> section.isSameUpStationId(stationId))
                .orElseThrow(() -> new SubwayException("해당 역을 상행역으로 가지는 구간이 존재하지 않습니다. 역 ID : " + stationId));
    }

    public boolean isEqualSizeToOne() {
        return this.values.size() == 1;
    }

    public boolean isFirstStation(final Long stationId) {
        final Long firstUpStationId = this.values.get(0).getUpStationId();

        return firstUpStationId.equals(stationId);
    }

    public boolean isLastStation(final Long stationId) {
        final Long lastDownStationId = this.values.get(this.values.size() - 1).getDownStationId();

        return lastDownStationId.equals(stationId);
    }

    public Section getContainStationSection(
            final Long upStationId,
            final Long downStationId
    ) {
        return find(this.values, section -> section.containsStations(upStationId, downStationId)).orElseThrow(
                () -> new SubwayException(
                        "상행 역과 하행 역이 모두 노선에 없습니다. 상행 역 ID : " + upStationId + " 하행 역 ID : " + downStationId));
    }

    public List<Long> getStationIds() {
        return this.stationIds;
    }

    private List<Section> getSortedSections(final List<Section> values) {
        final Map<Long, Section> upStationKeyMap = values.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));

        final Section firstSection = findFirstSectionInNotSortedSections(values)
                .orElseThrow(() -> new SubwayException("노선에 구간이 존재하지 않습니다."));

        final List<Section> result = new ArrayList<>(List.of(firstSection));
        Section next = upStationKeyMap.get(firstSection.getDownStationId());

        while (next != null) {
            result.add(next);
            next = upStationKeyMap.get(next.getDownStationId());
        }

        return result;
    }

    private Optional<Section> findFirstSectionInNotSortedSections(final List<Section> values) {
        final Set<Long> upStationIds = toSetWithMapper(values, Section::getUpStationId);
        final Set<Long> downStationIds = toSetWithMapper(values, Section::getDownStationId);

        upStationIds.removeAll(intersection(upStationIds, downStationIds));

        return find(values, section -> upStationIds.contains(section.getUpStationId()));
    }

    private List<Long> sortedStationIds() {
        final List<Long> result = new ArrayList<>(List.of(this.values.get(0).getUpStationId()));
        for (Section section : this.values) {
            result.add(section.getDownStationId());
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
}
