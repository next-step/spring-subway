package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    public static final int MIN_SECTION_COUNT = 1;
    private final List<Section> values;

    public Sections(List<Section> candidateValues) {
        validateNullOrEmpty(candidateValues);
        validateAllBelongToSameLine(candidateValues);
        this.values = sort(candidateValues);

        validateConnectedSections(candidateValues, this.values);
    }

    private void validateNullOrEmpty(List<Section> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("구간이 하나 이상 존재해야 합니다.");
        }
    }

    private void validateConnectedSections(List<Section> values, List<Section> sortedValues) {
        if (sortedValues.size() != values.size()) {
            throw new IllegalArgumentException("끊어진 구간을 입력했습니다.");
        }
    }

    private void validateAllBelongToSameLine(List<Section> values) {
        final long distinctLineCount = values.stream()
            .map(Section::getLine)
            .distinct()
            .count();

        if (distinctLineCount > 1) {
            throw new IllegalArgumentException("구간들은 모두 하나의 노선에 포함되어야 합니다.");
        }
    }

    private List<Section> sort(List<Section> values) {

        Map<Station, Section> nextSectionMap = values.stream()
            .collect(Collectors.toMap(Section::getUpStation, Function.identity()));

        return reorderSections(values, nextSectionMap);
    }

    private List<Section> reorderSections(List<Section> values,
        Map<Station, Section> nextSectionMap) {

        return Stream.iterate(findFirstSection(values),
                section -> getNextSection(section, nextSectionMap))
            .takeWhile(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Section getNextSection(Section section, Map<Station, Section> nextSectionMap) {
        return nextSectionMap.get(section.getDownStation());
    }

    private Section findFirstSection(List<Section> values) {

        Set<Station> downStations = extractDownStations(values);

        return values.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("노선은 순환할 수 없습니다."));
    }

    private Set<Station> extractDownStations(List<Section> values) {
        return values.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }

    public Optional<Section> add(Section section) {
        validateSectionInLine(section);
        validateOnlyOneStationInLine(section);

        if (section.isUpperSectionOf(getFirst())) {
            addFirst(section);
            return Optional.empty();
        }

        if (getLast().isUpperSectionOf(section)) {
            addLast(section);
            return Optional.empty();
        }

        return addSectionInMiddle(section);
    }

    private void addFirst(final Section section) {
        this.values.add(0, section);
    }

    private boolean addLast(final Section section) {
        return this.values.add(section);
    }

    private void validateSectionInLine(final Section section) {
        if (!section.belongTo(getFirst().getLine())) {
            throw new IllegalArgumentException("추가할 구간은 기존 노선에 포함되어야 합니다.");
        }
    }

    private Optional<Section> addSectionInMiddle(Section section) {
        Section mergeTarget = findMatchedSection(section);
        List<Section> mergeResults = mergeTarget.mergeSections(section);

        int insertIndex = this.values.indexOf(mergeTarget);
        this.values.remove(mergeTarget);
        this.values.addAll(insertIndex, mergeResults);

        return findUpdatedSection(mergeResults);
    }

    private static Optional<Section> findUpdatedSection(final List<Section> mergeResults) {
        return mergeResults.stream()
            .filter(result -> Objects.nonNull(result.getId()))
            .findAny();
    }

    private Section findMatchedSection(Section section) {
        return this.values.stream()
            .filter(value -> value.hasSameUpStationOrDownStation(section))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("구간 추가 도중 문제가 발생했습니다."));
    }

    private void validateOnlyOneStationInLine(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        boolean upStationExists = this.values.stream()
            .anyMatch(value -> value.containsStation(upStation));
        boolean downStationExists = this.values.stream()
            .anyMatch(value -> value.containsStation(downStation));

        validateBothStationsInLine(upStationExists, downStationExists);
        validateBothStationsNotInLine(upStationExists, downStationExists);
    }

    private void validateBothStationsInLine(final boolean upStationExists,
        final boolean downStationExists) {
        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다.");
        }
    }

    private void validateBothStationsNotInLine(final boolean upStationExists,
        final boolean downStationExists) {
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다.");
        }
    }

    public Optional<Section> remove(Station station) {
        validateSize();
        validateStationInLine(station);

        if (isLastInLine(station)) {
            removeLast();
            return Optional.empty();
        }

        if (isFirstInLine(station)) {
            removeFirst();
            return Optional.empty();
        }

        return removeInMiddle(station);
    }

    private Section removeFirst() {
        return values.remove(0);
    }

    private void removeLast() {
        values.remove(values.size() - 1);
    }

    private boolean isFirstInLine(final Station station) {
        return getFirst().hasUpStationSameAs(station);
    }

    private boolean isLastInLine(final Station station) {
        return getLast().hasDownStationSameAs(station);
    }

    private Optional<Section> removeInMiddle(final Station station) {
        Section upperSection = findUpperSectionOf(station);
        Section lowerSection = findLowerSectionOf(station);
        Section rearrangedSection = upperSection.rearrangeSections(lowerSection);

        int insertLocation = values.indexOf(upperSection);
        values.removeAll(List.of(upperSection, lowerSection));
        values.add(insertLocation, rearrangedSection);

        return Optional.of(rearrangedSection);
    }

    private Section findUpperSectionOf(final Station station) {
        return values.stream()
            .filter(section -> section.hasDownStationSameAs(station))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 역을 하행역으로 가지는 구간을 찾을 수 없습니다."));
    }

    private Section findLowerSectionOf(final Station station) {
        return values.stream()
            .filter(section -> section.hasUpStationSameAs(station))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 역을 상행역으로 가지는 구간을 찾을 수 없습니다."));
    }

    private void validateStationInLine(final Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("노선에 등록되어 있지 않은 역은 제거할 수 없습니다.");
        }
    }

    private void validateSize() {
        if (values.size() <= MIN_SECTION_COUNT) {
            throw new IllegalStateException("노선의 구간이 " + MIN_SECTION_COUNT + "개인 경우 삭제할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = values.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(getLast().getDownStation());
        return stations;
    }

    private Section getFirst() {
        return this.values.get(0);
    }


    private Section getLast() {
        return this.values.get(this.values.size() - 1);
    }

    public List<Section> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sections)) {
            return false;
        }
        Sections sections = (Sections) o;
        return Objects.equals(values, sections.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "values=" + values +
            '}';
    }
}
