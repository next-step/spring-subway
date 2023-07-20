package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.vo.SectionAdditionResult;

public class Sections {

    public static final int MIN_SECTION_SIZE = 1;
    private final List<Section> values;

    public Sections(List<Section> values) {
        validateNullOrEmpty(values);
        this.values = sort(values);

        validateConnectedSections(values, this.values);
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

    public boolean isSectionsAllBelongTo(Line line) {
        return values.stream()
            .allMatch(section -> section.belongTo(line));
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

    private static Section getNextSection(Section section, Map<Station, Section> nextSectionMap) {
        return nextSectionMap.get(section.getDownStation());
    }

    private Section findFirstSection(List<Section> values) {

        List<Station> downStations = extractDownStations(values);

        return values.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("노선은 순환할 수 없습니다."));
    }

    private List<Station> extractDownStations(List<Section> values) {
        return values.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    public SectionAdditionResult add(Section section) {
        validateOneInOneOut(section);

        if (section.canPrecede(getFirst())) {
            addFirst(section);
            return new SectionAdditionResult(Optional.empty(), List.of(getFirst()));
        }

        if (getLast().canPrecede(section)) {
            addLast(section);
            return new SectionAdditionResult(Optional.empty(), List.of(getLast()));
        }

        return addSectionInMiddle(section);
    }

    private void addLast(Section section) {
        this.values.add(section);
    }

    private SectionAdditionResult addSectionInMiddle(Section section) {
        Section foundSection = findMatchedSection(section);
        List<Section> sectionsToAdd = foundSection.mergeSections(section);

        this.values.addAll(this.values.indexOf(foundSection), sectionsToAdd);
        this.values.remove(foundSection);

        return new SectionAdditionResult(Optional.of(foundSection), sectionsToAdd);
    }

    private Section findMatchedSection(Section section) {
        return this.values.stream()
            .filter(value -> value.hasSameUpStationOrDownStation(section))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("여기 오면 안되는데?"));
    }

    private void validateOneInOneOut(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        boolean upStationExists = isStationExists(upStation);
        boolean downStationExists = isStationExists(downStation);

        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다. upStation: " + upStation + " downStation: " + downStation);
        }
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다. upStation: " + upStation + " downStation: " + downStation);
        }
    }

    private boolean isStationExists(Station upStation) {
        return this.values.stream()
            .anyMatch(value -> value.containsStation(upStation));
    }

    private void addFirst(Section section) {
        this.values.add(0, section);
    }

    public Section removeLast(Station station) {
        validateMinSectionSize();
        validateFinalDownStationSameAs(station);

        return values.remove(values.size() - 1);
    }

    private void validateFinalDownStationSameAs(Station station) {
        if (!getLast().hasDownStationSameAs(station)) {
            throw new IllegalArgumentException(
                "삭제할 역이 해당 노선의 하행종점역이 아닙니다 요청 station: " + station + " 하행 종점 구간 : " + getLast());
        }
    }

    private void validateMinSectionSize() {
        if (values.size() <= MIN_SECTION_SIZE) {
            throw new IllegalStateException("노선의 구간이 1개인 경우 삭제할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = values.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(getLast().getDownStation());
        return stations;
    }

    Section getFirst() {
        return this.values.get(0);
    }


    Section getLast() {
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
