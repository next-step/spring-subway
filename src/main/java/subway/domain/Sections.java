package subway.domain;

import static java.util.Collections.unmodifiableList;
import static subway.util.CollectionUtil.toGroupByMap;
import static subway.util.CollectionUtil.toMappedList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.dto.SectionAdditionResult;
import subway.dto.SectionRemovalResult;

public class Sections {

    public static final int MIN_SECTION_SIZE = 1;
    private final List<Section> values;

    public Sections(List<Section> values) {
        validateNullOrEmpty(values);
        this.values = sort(values);

        validateConnectedSections(values);
    }

    private void validateNullOrEmpty(List<Section> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("구간이 하나 이상 존재해야 합니다.");
        }
    }

    private List<Section> sort(List<Section> values) {
        Map<Station, Section> sectionByUpStation = toGroupByMap(unmodifiableList(values),
            Section::getUpStation);

        return Stream.iterate(findFirstSection(values),
                section -> getNextSection(section, sectionByUpStation))
            .takeWhile(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Section findFirstSection(List<Section> values) {

        List<Station> downStations = toMappedList(unmodifiableList(values), Section::getDownStation);

        return values.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("노선은 순환할 수 없습니다."));
    }

    private static Section getNextSection(Section section, Map<Station, Section> nextSectionMap) {
        return nextSectionMap.get(section.getDownStation());
    }

    private void validateConnectedSections(List<Section> initialSections) {
        if (this.values.size() != initialSections.size()) {
            throw new IllegalArgumentException("끊어진 구간을 입력했습니다.");
        }
    }

    public boolean isSectionsAllBelongTo(Line line) {
        return values.stream()
            .allMatch(section -> section.belongTo(line));
    }

    public SectionAdditionResult add(Section section) {
        validateOnlyOneStationIncludedInSections(section);

        if (section.canPrecede(getFirst())) {
            addFirst(section);
            return new SectionAdditionResult(null, List.of(getFirst()));
        }

        if (getLast().canPrecede(section)) {
            addLast(section);
            return new SectionAdditionResult(null, List.of(getLast()));
        }

        if (isMiddleAddableSection(section)) {
            return addSectionInMiddle(section);
        }

        throw new IllegalStateException("예상하지 못한 경우입니다.");
    }

    private void validateOnlyOneStationIncludedInSections(Section section) {
        checkBothStationsInLine(section);
        checkNoneOfStationsInLine(section);
    }

    private void checkBothStationsInLine(Section section) {
        if (isStationExists(section.getUpStation()) && isStationExists(section.getDownStation())) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다. upStation: " + section.getUpStation() + " downStation: " + section.getDownStation());
        }
    }

    private void checkNoneOfStationsInLine(Section section) {
        if (!isStationExists(section.getUpStation()) && !isStationExists(section.getDownStation())) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다. upStation: " + section.getUpStation() + " downStation: " + section.getDownStation());
        }
    }

    private void addFirst(Section section) {
        this.values.add(0, section);
    }

    private void addLast(Section section) {
        this.values.add(section);
    }

    private boolean isMiddleAddableSection(Section section) {
        return this.values.stream()
                .anyMatch(value -> value.hasSameUpStationOrDownStation(section));
    }

    private SectionAdditionResult addSectionInMiddle(Section section) {
        Section foundSection = findMatchedSectionWithAnyStation(section);
        List<Section> sectionsToAdd = foundSection.mergeWith(section);

        this.values.addAll(this.values.indexOf(foundSection), sectionsToAdd);
        this.values.remove(foundSection);

        return new SectionAdditionResult(foundSection, sectionsToAdd);
    }

    private Section findMatchedSectionWithAnyStation(Section section) {
        return this.values.stream()
            .filter(value -> value.hasSameUpStationOrDownStation(section))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("예상하지 못한 에러입니다."));
    }

    private boolean isStationExists(Station upStation) {
        return this.values.stream()
            .anyMatch(value -> value.containsStation(upStation));
    }

    public SectionRemovalResult remove(Station station) {
        validateExistsInSections(station);
        validateMinSectionSize();

        if (isFinalUpStation(station)) {
            return new SectionRemovalResult(null, List.of(removeFirst()));
        }

        if (isFinalDownStation(station)) {
            return new SectionRemovalResult(null, List.of(removeLast()));
        }

        if (isMiddleStation(station)) {
            return removeMiddleStation(station);
        }

        throw new IllegalStateException("예상하지 못한 경우입니다.");
    }

    private void validateExistsInSections(Station station) {
        if (!isStationExists(station)) {
            throw new IllegalArgumentException("삭제할 역이 노선 내에 존재하지 않습니다. 삭제할 역: " + station);
        }
    }

    private void validateMinSectionSize() {
        if (values.size() <= MIN_SECTION_SIZE) {
            throw new IllegalStateException("노선의 구간이 1개인 경우 삭제할 수 없습니다.");
        }
    }

    private boolean isFinalUpStation(Station station) {
        return getFirst().hasUpStationSameAs(station);
    }

    private Section removeFirst() {
        return values.remove(0);
    }

    private boolean isFinalDownStation(Station station) {
        return getLast().hasDownStationSameAs(station);
    }

    private Section removeLast() {
        return values.remove(values.size() - 1);
    }

    private boolean isMiddleStation(Station station) {
        return this.values.stream()
                .anyMatch(section -> section.hasDownStationSameAs(station)) &&
                !getLast().hasDownStationSameAs(station);
    }

    private SectionRemovalResult removeMiddleStation(Station station) {
        Section matchedSection = findMatchedSectionSameDownStation(station);
        Section nextSection = findNextSectionOf(matchedSection);

        Section connectedSection = matchedSection.removeMiddleStation(nextSection);
        this.values.add(this.values.indexOf(matchedSection), connectedSection);
        this.values.remove(matchedSection);
        this.values.remove(nextSection);

        List<Section> sectionToRemove = List.of(matchedSection, nextSection);
        return new SectionRemovalResult(connectedSection, sectionToRemove);
    }

    private Section findMatchedSectionSameDownStation(Station station) {
        return this.values.stream()
            .filter(section -> section.hasDownStationSameAs(station))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("예상하지 못한 에러입니다"));
    }

    private Section findNextSectionOf(Section matchedSection) {
        return this.values.get(this.values.indexOf(matchedSection) + 1);
    }

    public List<Station> getStations() {
        List<Station> stations = toMappedList(unmodifiableList(values), Section::getUpStation);
        stations.add(getLast().getDownStation());
        return stations;
    }

    private Section getFirst() {
        return this.values.get(0);
    }

    private Section getLast() {
        return this.values.get(this.values.size() - 1);
    }

    public int getTotalDistance() {
        return this.values.stream()
            .mapToInt(Section::getDistance)
            .sum();
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
