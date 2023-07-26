package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.CollectionUtils;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> candidateValues) {
        validateNullOrEmpty(candidateValues);
        validateAllBelongToSameLine(candidateValues);
        this.sections = sort(candidateValues);

        validateConnectedSections(candidateValues, this.sections);
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

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(getLast().getDownStation());
        return stations;
    }

    public boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    public boolean isFirst(Station station) {
        return getFirst().hasUpStation(station);
    }

    public boolean isLast(Station station) {
        return getLast().hasDownStation(station);
    }

    public Optional<Section> filter(Predicate<Section> condition) {
        return sections.stream()
            .filter(condition)
            .findAny();
    }

    public int getSize() {
        return sections.size();
    }

    private Section getFirst() {
        return CollectionUtils.firstElement(sections);
    }


    private Section getLast() {
        return CollectionUtils.lastElement(sections);
    }

    public List<Section> getSections() {
        return sections;
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
        return Objects.equals(this.sections, sections.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "values=" + sections +
            '}';
    }
}
