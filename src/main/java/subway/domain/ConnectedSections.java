package subway.domain;

import subway.exception.SubwayIllegalArgumentException;
import subway.utils.SetUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ConnectedSections {

    private final List<Section> connectedSections;

    public ConnectedSections(final List<Section> sections) {
        validateHasSectionAndAllSameLine(sections);

        this.connectedSections = convertToConnectedSections(sections);
    }

    public List<Section> getConnectedSections() {
        return Collections.unmodifiableList(this.connectedSections);
    }

    public SectionEditResult add(final Section target) {
        validateAddSectionCondition(target);

        if (isFirstSection(target)) {
            return addOnFirstSection(target);
        }
        if (isLastSection(target)) {
            return addOnLastSection(target);
        }

        return addOnBetween(target);
    }

    public SectionEditResult remove(final Long targetStationId) {
        validateRemoveSectionCondition(targetStationId);

        final Section removed = connectedSections.remove(connectedSections.size() - 1);

        return new SectionEditResult(
                Collections.emptySet(),
                Set.of(removed)
        );
    }

    public List<Long> getSortedStationIds() {
        final List<Long> sortedStationIds = connectedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        sortedStationIds.add(getLastSection().getDownStationId());

        return sortedStationIds;
    }

    private List<Section> convertToConnectedSections(final List<Section> sections) {
        final Set<Long> upStationIds = SetUtils.toSet(sections, Section::getUpStationId);
        final Set<Long> downStationIds = SetUtils.toSet(sections, Section::getDownStationId);

        validateConnectedSections(sections, SetUtils.union(upStationIds, downStationIds));

        final Long firstStationId = extractFirstStationId(SetUtils.subtract(upStationIds, downStationIds));
        return generateConnectedSections(sections, firstStationId);
    }

    private List<Section> generateConnectedSections(final List<Section> sections, final Long firstStationId) {
        final Map<Long, Section> sectionByUpStationId = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));

        return Stream.iterate(sectionByUpStationId.get(firstStationId),
                        Objects::nonNull,
                        section -> sectionByUpStationId.get(section.getDownStationId()))
                .collect(Collectors.toList());
    }

    private Long extractFirstStationId(final Set<Long> uniqueUpStationIds) {
        return uniqueUpStationIds.stream()
                .findAny()
                .orElseThrow(() -> new SubwayIllegalArgumentException("상행 종점역을 찾을 수 없습니다."));
    }

    private SectionEditResult addOnLastSection(final Section target) {
        connectedSections.add(target);
        return new SectionEditResult(Set.of(target), Collections.emptySet());
    }

    private SectionEditResult addOnFirstSection(final Section target) {
        connectedSections.add(0, target);
        return new SectionEditResult(Set.of(target), Collections.emptySet());
    }

    private SectionEditResult addOnBetween(final Section target) {
        final OptionalInt upStationAddIndex = findUpStationAddIndex(target);
        if (upStationAddIndex.isPresent()) {
            return addOnUpStation(upStationAddIndex.getAsInt(), target);
        }

        final OptionalInt downStationAddIndex = findDownStationAddIndex(target);
        if (downStationAddIndex.isPresent()) {
            return addOnDownStation(downStationAddIndex.getAsInt(), target);
        }

        throw new SubwayIllegalArgumentException(
                "구간을 추가할 수 없습니다. 상행역: " + target.getUpStationId() + ", 하행역:" + target.getDownStationId()
        );
    }

    private OptionalInt findUpStationAddIndex(final Section target) {
        return IntStream.range(0, connectedSections.size())
                .filter(i -> getSection(i).isSameUpStationId(target))
                .findAny();
    }

    private OptionalInt findDownStationAddIndex(final Section target) {
        return IntStream.range(0, connectedSections.size())
                .filter(i -> getSection(i).isSameDownStationId(target))
                .findAny();
    }

    /**
     * {@link #addOnDownStation(int, Section)}와 대부분의 코드가 동일하지만 구간을 추가하는 순서만 다르다.
     */
    private SectionEditResult addOnUpStation(final int index, final Section target) {
        final Section current = getSection(index);
        validateDistance(current, target);

        final Section subtracted = current.subtract(target);
        final Section removed = connectedSections.remove(index);
        connectedSections.add(index, subtracted);
        connectedSections.add(index, target);

        return new SectionEditResult(
                Set.of(subtracted, target),
                Set.of(removed)
        );
    }

    /**
     * {@link #addOnUpStation(int, Section)}와 대부분의 코드가 동일하지만 구간을 추가하는 순서만 다르다.
     */
    private SectionEditResult addOnDownStation(final int index, final Section target) {
        final Section current = getSection(index);
        validateDistance(current, target);

        final Section subtracted = current.subtract(target);
        final Section removed = connectedSections.remove(index);
        connectedSections.add(index, target);
        connectedSections.add(index, subtracted);

        return new SectionEditResult(
                Set.of(target, subtracted),
                Set.of(removed)
        );
    }

    private void validateHasSectionAndAllSameLine(final List<Section> sections) {
        validateHaveSection(sections);
        validateAllSameLine(sections);
    }

    private void validateHaveSection(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SubwayIllegalArgumentException("구간은 최소 하나 이상 있어야합니다.");
        }
    }

    private void validateAllSameLine(final List<Section> sections) {
        final long lineCount = sections.stream()
                .map(Section::getLineId)
                .distinct()
                .count();

        if (lineCount != 1) {
            throw new SubwayIllegalArgumentException("구간은 모두 같은 노선에 있어야합니다.");
        }
    }

    private void validateConnectedSections(final List<Section> sections, final Set<Long> stationIds) {
        if (stationIds.size() - 1 != sections.size()) {
            throw new SubwayIllegalArgumentException("구간이 모두 연결되어있지 않습니다.");
        }
    }

    private void validateDistance(final Section current, final Section target) {
        if (current.subtractDistance(target) <= 0) {
            throw new SubwayIllegalArgumentException("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        }
    }

    private void validateAddSectionCondition(final Section target) {
        if (containsAll(target)) {
            throw new SubwayIllegalArgumentException("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
        }
        if (containsAny(target)) {
            throw new SubwayIllegalArgumentException("상행 역과 하행 역이 모두 노선에 없습니다.");
        }
    }

    private void validateRemoveSectionCondition(final Long targetStationId) {
        if (connectedSections.size() == 1) {
            throw new SubwayIllegalArgumentException("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
        }
        if (getLastSection().doesNotContainsDownStation(targetStationId)) {
            throw new SubwayIllegalArgumentException("해당 노선에 일치하는 하행 종점역이 존재하지 않습니다.");
        }
    }

    private boolean isFirstSection(final Section target) {
        return target.isHead(getFirstSection());
    }

    private boolean isLastSection(final Section target) {
        return getLastSection().isHead(target);
    }

    private boolean containsAll(final Section target) {
        final Set<Long> stationIds = getStationIds();

        return stationIds.contains(target.getUpStationId()) && stationIds.contains(target.getDownStationId());
    }

    private boolean containsAny(final Section target) {
        final Set<Long> stationIds = getStationIds();

        return !stationIds.contains(target.getUpStationId()) && !stationIds.contains(target.getDownStationId());
    }

    private Section getSection(final int index) {
        return connectedSections.get(index);
    }

    private Section getFirstSection() {
        return getSection(0);
    }

    private Section getLastSection() {
        return getSection(connectedSections.size() - 1);
    }

    private Set<Long> getStationIds() {
        return SetUtils.union(
                SetUtils.toSet(connectedSections, Section::getUpStationId),
                SetUtils.toSet(connectedSections, Section::getDownStationId)
        );
    }
}
