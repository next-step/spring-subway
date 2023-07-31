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

    private static final int MIN_SECTION_COUNT = 1;

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

    public void validateAddition(final Station upStation, final Station downStation, final int distance) {
        validateLineHasOneOf(upStation, downStation);

        filter(matchOneOf(upStation, downStation))
            .ifPresent(section -> validateDistance(section, distance));
    }

    private void validateDistance(final Section section, final int distance) {
        if (section.isNotLongerThan(distance)) {
            throw new IllegalArgumentException("추가할 구간의 크기가 너무 큽니다.");
        }
    }

    public boolean isAddedInMiddle(Section newSection) {
        return filter(section -> section.matchEitherStation(newSection)).isPresent();
    }

    public Section findSectionToChange(Section newSection) {
        return filter(section -> section.matchEitherStation(newSection))
            .map(section -> section.cutBy(newSection))
            .orElseThrow(() -> new IllegalArgumentException("구간 추가로 인해 변경된 구간이 존재하지 않습니다."));
    }

    private Predicate<Section> matchOneOf(final Station upStation, final Station downStation) {
        return section -> section.hasUpStation(upStation) || section.hasDownStation(downStation);
    }

    private void validateLineHasOneOf(final Station upStation, final Station downStation) {
        boolean hasUpStation = hasStation(upStation);
        boolean hasDownStation = hasStation(downStation);

        validateBoth(hasUpStation, hasDownStation);
        validateNotBoth(hasUpStation, hasDownStation);
    }

    private void validateBoth(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다.");
        }
    }

    private void validateNotBoth(final boolean hasUpStation, final boolean hasDownStation) {
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다.");
        }
    }

    public void validateRemoval(final Station station) {
        validateSize();
        validateStationInLine(station);
    }

    private void validateSize() {
        if (sections.size() <= MIN_SECTION_COUNT) {
            throw new IllegalStateException("노선의 구간이 " + MIN_SECTION_COUNT + "개인 경우 삭제할 수 없습니다.");
        }
    }

    private void validateStationInLine(final Station station) {
        if (!hasStation(station)) {
            throw new IllegalArgumentException("노선에 등록되어 있지 않은 역은 제거할 수 없습니다.");
        }
    }

    public boolean isInMiddle(final Station station) {
        return !isAtEndOfLine(station);
    }

    public Section findSectionToChange(final Station station) {
        if (isAtEndOfLine(station)) {
            throw new IllegalArgumentException("구간 삭제로 인해 변경된 구간이 존재하지 않습니다.");
        }

        Section upperSection = getUpperSection(station);
        Section lowerSection = getLowerSection(station);
        return upperSection.extendBy(lowerSection);
    }

    private boolean isAtEndOfLine(final Station station) {
        return isFirst(station) || isLast(station);
    }

    private Section getUpperSection(final Station station) {
        return filter(section -> section.hasDownStation(station))
            .orElseThrow(() -> new IllegalStateException("역의 위 구간을 찾을 수 없습니다."));
    }

    private Section getLowerSection(final Station station) {
        return filter(section -> section.hasUpStation(station))
            .orElseThrow(() -> new IllegalStateException("역의 아래 구간을 찾을 수 없습니다."));
    }

    private boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    private boolean isFirst(Station station) {
        return getFirst().hasUpStation(station);
    }

    private boolean isLast(Station station) {
        return getLast().hasDownStation(station);
    }

    private Section getFirst() {
        return CollectionUtils.firstElement(sections);
    }


    private Section getLast() {
        return CollectionUtils.lastElement(sections);
    }

    private Optional<Section> filter(Predicate<Section> condition) {
        return sections.stream()
            .filter(condition)
            .findAny();
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(getLast().getDownStation());
        return stations;
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
