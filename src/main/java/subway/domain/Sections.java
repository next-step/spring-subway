package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public SectionChange add(Section section) {
        validateSectionInLine(section);
        validateOnlyOneStationInLine(section);

        if (section.isUpperSectionOf(getFirst())) {
            return addFirst(section);
        }

        if (getLast().isUpperSectionOf(section)) {
            return addLast(section);
        }

        return addSectionInMiddle(section);
    }

    private SectionChange addFirst(final Section section) {
        this.values.add(0, section);
        SectionChange sectionChange = new SectionChange();
        sectionChange.appendSectionToAdd(section);
        return sectionChange;
    }

    private SectionChange addLast(final Section section) {
        this.values.add(section);
        SectionChange sectionChange = new SectionChange();
        sectionChange.appendSectionToAdd(section);
        return sectionChange;
    }

    private void validateSectionInLine(final Section section) {
        if (!section.belongTo(getFirst().getLine())) {
            throw new IllegalArgumentException("추가할 구간은 기존 노선에 포함되어야 합니다.");
        }
    }

    private SectionChange addSectionInMiddle(Section section) {
        Section foundSection = findMatchedSection(section);
        List<Section> sectionsToAdd = foundSection.mergeSections(section);

        this.values.addAll(this.values.indexOf(foundSection), sectionsToAdd);
        this.values.remove(foundSection);

        final SectionChange sectionChange = new SectionChange();
        sectionChange.appendSectionToRemove(foundSection);
        sectionChange.appendSectionsToAdd(sectionsToAdd);

        return sectionChange;
    }

    private Section findMatchedSection(Section section) {
        return this.values.stream()
            .filter(value -> value.hasSameUpStationOrDownStation(section))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("여기 오면 안되는데?"));
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

    private static void validateBothStationsInLine(final boolean upStationExists, final boolean downStationExists) {
        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다.");
        }
    }

    private static void validateBothStationsNotInLine(final boolean upStationExists, final boolean downStationExists) {
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다.");
        }
    }

    public Section removeLast(Station station) {
        validateSize();
        validateFinalDownStationSameAs(station);

        return values.remove(values.size() - 1);
    }

    private void validateFinalDownStationSameAs(Station station) {
        if (!getLast().hasDownStationSameAs(station)) {
            throw new IllegalArgumentException(
                "삭제할 역이 해당 노선의 하행종점역이 아닙니다 요청 station: " + station + " 하행 종점 구간 : " + getLast());
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
