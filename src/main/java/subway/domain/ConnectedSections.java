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

    private final List<Section> values;

    public ConnectedSections(final List<Section> sections) {
        validateHaveSection(sections);
        validateAllSameLine(sections);

        this.values = convertToConnectedSections(sections);
    }

    public List<Section> getConnectedSections() {
        return Collections.unmodifiableList(this.values);
    }

    public List<Long> getConnectedStationIds() {
        final List<Long> sortedStationIds = values.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        sortedStationIds.add(getLastSection().getDownStationId());

        return sortedStationIds;
    }

    public SectionEditResult add(final Section target) {
        validateContainsJustOneStation(target);

        if (target.isConnectedForward(getFirstSection())) {
            values.add(0, target);
            return new SectionEditResult(List.of(target), Collections.emptyList());
        }
        if (target.isConnectedBack(getLastSection())) {
            values.add(target);
            return new SectionEditResult(List.of(target), Collections.emptyList());
        }

        return addOnBetween(target);
    }

    public SectionEditResult remove(final Long targetStationId) {
        validateValueIsNotEmptyAndTargetStationExists(targetStationId);

        if (getFirstSection().isSameUpStationId(targetStationId)) {
            final Section removed = removeFirstSection();
            return new SectionEditResult(
                    Collections.emptyList(),
                    List.of(removed)
            );
        }
        if (getLastSection().isSameDownStationId(targetStationId)) {
            final Section removed = removeLastSection();
            return new SectionEditResult(
                    Collections.emptyList(),
                    List.of(removed)
            );
        }

        return removeBetween(targetStationId);
    }

    private List<Section> convertToConnectedSections(final List<Section> sections) {
        final Set<Long> upStationIds = SetUtils.toSet(sections, Section::getUpStationId);
        final Set<Long> downStationIds = SetUtils.toSet(sections, Section::getDownStationId);

        validateAllSectionIsConnected(sections, SetUtils.union(upStationIds, downStationIds));

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

    private SectionEditResult addOnBetween(final Section target) {
        final Long upStationId = target.getUpStationId();
        final OptionalInt upStationAddIndex = findUpStationAddIndex(upStationId);
        if (upStationAddIndex.isPresent()) {
            return addOnUpStation(upStationAddIndex.getAsInt(), target);
        }

        final Long downStationId = target.getDownStationId();
        final OptionalInt downStationAddIndex = findDownStationAddIndex(downStationId);
        if (downStationAddIndex.isPresent()) {
            return addOnDownStation(downStationAddIndex.getAsInt(), target);
        }

        throw new SubwayIllegalArgumentException(
                "구간을 추가할 수 없습니다. 상행역: " + target.getUpStationId() + ", 하행역:" + target.getDownStationId()
        );
    }

    private OptionalInt findUpStationAddIndex(final Long upStationId) {
        return IntStream.range(0, values.size())
                .filter(index -> getSection(index).isSameUpStationId(upStationId))
                .findAny();
    }

    private OptionalInt findDownStationAddIndex(final Long downStationId) {
        return IntStream.range(0, values.size())
                .filter(index -> getSection(index).isSameDownStationId(downStationId))
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
        return IntStream.range(0, values.size())
                .filter(mapper)
                .mapToObj(values::remove)
                .findAny()
                .orElseThrow(() ->
                        new SubwayIllegalArgumentException("삭제할 역의 구간을 찾지 못하였습니다.")
                );
    }

    private Section removeFirstSection() {
        return values.remove(0);
    }

    private Section removeLastSection() {
        return values.remove(values.size() - 1);
    }

    /**
     * {@link #addOnDownStation(int, Section)}와 대부분의 코드가 동일하지만 구간을 추가하는 순서만 다르다.
     */
    private SectionEditResult addOnUpStation(final int index, final Section target) {
        final Section subtracted = getSection(index).subtract(target);
        final Section removed = values.remove(index);
        values.add(index, subtracted);
        values.add(index, target);

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
        final Section removed = values.remove(index);
        values.add(index, target);
        values.add(index, subtracted);

        return new SectionEditResult(
                List.of(subtracted, target),
                List.of(removed)
        );
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

    private void validateAllSectionIsConnected(final List<Section> sections, final Set<Long> stationIds) {
        if (stationIds.size() - 1 != sections.size()) {
            throw new SubwayIllegalArgumentException("구간이 모두 연결되어있지 않습니다.");
        }
    }

    private void validateContainsJustOneStation(final Section target) {
        final Long upStationId = target.getUpStationId();
        final Long downStationId = target.getDownStationId();

        final Set<Long> stationIds = getStationIds();
        if (stationIds.contains(upStationId) && stationIds.contains(downStationId)) {
            throw new SubwayIllegalArgumentException("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
        }
        if (!stationIds.contains(upStationId) && !stationIds.contains(downStationId)) {
            throw new SubwayIllegalArgumentException("상행 역과 하행 역이 모두 노선에 없습니다.");
        }
    }

    private void validateValueIsNotEmptyAndTargetStationExists(final Long targetStationId) {
        if (values.size() == 1) {
            throw new SubwayIllegalArgumentException("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
        }
        if (!getStationIds().contains(targetStationId)) {
            throw new SubwayIllegalArgumentException("삭제하려는 역이 전체 구간에 존재하지 않습니다. 삭제하려는 역: " + targetStationId);
        }
    }

    private Section getSection(final int index) {
        return values.get(index);
    }

    private Section getFirstSection() {
        return getSection(0);
    }

    private Section getLastSection() {
        return getSection(values.size() - 1);
    }

    private Set<Long> getStationIds() {
        return SetUtils.union(
                SetUtils.toSet(values, Section::getUpStationId),
                SetUtils.toSet(values, Section::getDownStationId)
        );
    }
}
