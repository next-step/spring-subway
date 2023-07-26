package subway.domain;

import subway.exception.SubwayIllegalArgumentException;
import subway.utils.SetUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
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

    public List<Long> getSortedStationIds() {
        final List<Long> sortedStationIds = connectedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        sortedStationIds.add(getLastSection().getDownStationId());

        return sortedStationIds;
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

        if (isRemovableFirstSection(targetStationId)) {
            return new SectionEditResult(
                    Collections.emptyList(),
                    List.of(removeFirstSection())
            );
        }
        if (isRemovableLastSection(targetStationId)) {
            return new SectionEditResult(
                    Collections.emptyList(),
                    List.of(removeLastSection())
            );
        }

        return removeBetween(targetStationId);
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
        return new SectionEditResult(List.of(target), Collections.emptyList());
    }

    private SectionEditResult addOnFirstSection(final Section target) {
        connectedSections.add(0, target);
        return new SectionEditResult(List.of(target), Collections.emptyList());
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
                .filter(i -> getSection(i).isSameUpStation(target))
                .findAny();
    }

    private OptionalInt findDownStationAddIndex(final Section target) {
        return IntStream.range(0, connectedSections.size())
                .filter(i -> getSection(i).isSameDownStation(target))
                .findAny();
    }

    private SectionEditResult removeBetween(final Long targetStationId) {
        final Section upSection = removeWithMapper(index -> getSection(index).isSameDownStationId(targetStationId));
        final Section downSection = removeWithMapper(index -> getSection(index).isSameUpStationId(targetStationId));

        final Section mergedSection = upSection.merge(downSection);

        return new SectionEditResult(
                List.of(mergedSection),
                List.of(upSection, downSection)
        );
    }

    private Section removeWithMapper(final IntPredicate mapper) {
        return IntStream.range(0, connectedSections.size())
                .filter(mapper)
                .mapToObj(connectedSections::remove)
                .findAny()
                .orElseThrow(() ->
                        new SubwayIllegalArgumentException("삭제할 역의 구간을 찾지 못하였습니다.")
                );
    }

    private Section removeFirstSection() {
        return connectedSections.remove(0);
    }

    private Section removeLastSection() {
        return connectedSections.remove(connectedSections.size() - 1);
    }

    private boolean isRemovableFirstSection(final Long targetStationId) {
        return getFirstSection().getUpStationId().equals(targetStationId);
    }

    private boolean isRemovableLastSection(final Long targetStationId) {
        return getLastSection().getDownStationId().equals(targetStationId);
    }

    /**
     * {@link #addOnDownStation(int, Section)}와 대부분의 코드가 동일하지만 구간을 추가하는 순서만 다르다.
     */
    private SectionEditResult addOnUpStation(final int index, final Section target) {
        final Section subtracted = getSection(index).subtract(target);
        final Section removed = connectedSections.remove(index);
        connectedSections.add(index, subtracted);
        connectedSections.add(index, target);

        return new SectionEditResult(
                List.of(target, subtracted),
                List.of(removed)
        );
    }

    /**
     * {@link #addOnUpStation(int, Section)}와 대부분의 코드가 동일하지만 구간을 추가하는 순서만 다르다.
     */
    private SectionEditResult addOnDownStation(final int index, final Section target) {
        final Section subtracted = getSection(index).subtract(target);
        final Section removed = connectedSections.remove(index);
        connectedSections.add(index, target);
        connectedSections.add(index, subtracted);

        return new SectionEditResult(
                List.of(subtracted, target),
                List.of(removed)
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
        if (!getStationIds().contains(targetStationId)) {
            throw new SubwayIllegalArgumentException("삭제하려는 역이 전체 구간에 존재하지 않습니다. 삭제하려는 역: " + targetStationId);
        }
    }

    private boolean isFirstSection(final Section target) {
        return target.isConnectedForward(getFirstSection());
    }

    private boolean isLastSection(final Section target) {
        return getLastSection().isConnectedForward(target);
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
