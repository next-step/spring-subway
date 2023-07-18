package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = sort(values);
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

        Set<Station> downStations = extractDownStations(values);

        return values.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findAny()
            .orElseThrow();
    }

    private Set<Station> extractDownStations(List<Section> values) {
        return values.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }

    public void addLast(Section section) {
        validateAddable(section);
        validateNotContainDownStationOf(section);
        this.values.add(section);
    }

    private void validateNotContainDownStationOf(Section section) {
        if (this.values.stream()
            .anyMatch(value -> value.containsDownStationOf(section))) {
            throw new IllegalArgumentException();
        }
    }

    private void validateAddable(Section section) {
        if (getLast().cannotPrecede(section)) {
            throw new IllegalArgumentException(
                "구간 끝에 추가할 수 없습니다. 기존 하행 구간: " + getLast() + " 추가할 구간: " + section);
        }
    }

    public Section removeLast() {
        if (values.size() <= 1) {
            throw new IllegalStateException("노선의 구간이 1개인 경우 삭제할 수 없습니다.");
        }
        return values.remove(values.size() - 1);
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
