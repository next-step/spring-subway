package subway.domain;

import static java.util.Collections.unmodifiableList;
import static subway.util.CollectionUtil.toGroupByMap;
import static subway.util.CollectionUtil.toMappedList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.dto.SectionAdditionResult;
import subway.dto.SectionRemovalResult;
import subway.util.CollectionUtil;

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

        List<Station> downStations = CollectionUtil.toMappedList(unmodifiableList(values), Section::getDownStation);

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
        Section relatedSection = getRelatedSection(section);
        List<Section> mergedSection = SectionMerger.merge(relatedSection, section);

        replace(relatedSection, mergedSection);

        return new SectionAdditionResult(relatedSection, mergedSection);
    }

    private void validateOnlyOneStationIncludedInSections(Section section) {
        checkBothStationsInLine(section);
        checkNoneOfStationsInLine(section);
    }

    private void checkBothStationsInLine(Section section) {
        if (isStationExists(section.getUpStation()) && isStationExists(section.getDownStation())) {
            throw new IllegalArgumentException(
                "두 역이 모두 노선에 포함되어 있습니다. upStation: " + section.getUpStation() + " downStation: "
                    + section.getDownStation());
        }
    }

    private void checkNoneOfStationsInLine(Section section) {
        if (!isStationExists(section.getUpStation()) && !isStationExists(
            section.getDownStation())) {
            throw new IllegalArgumentException(
                "두 역이 모두 노선에 포함되어 있지 않습니다. upStation: " + section.getUpStation() + " downStation: "
                    + section.getDownStation());
        }
    }

    private Section getRelatedSection(Section section) {

        Optional<Section> middleMergeableSectionOptional = this.values.stream()
            .filter(value -> value.isMiddleMergeable(section))
            .findAny();

        return middleMergeableSectionOptional.orElseGet(() ->
            this.values.stream()
            .filter(value -> value.isRelated(section))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("연관된 구역이 없습니다.")));
    }

    private void replace(Section relatedSection, List<Section> mergedSection) {
        int relatedSectionIndex = this.values.indexOf(relatedSection);
        this.values.remove(relatedSection);
        this.values.addAll(relatedSectionIndex, mergedSection);
    }

    private boolean isStationExists(Station upStation) {
        return this.values.stream()
            .anyMatch(value -> value.containsStation(upStation));
    }

    public SectionRemovalResult remove(Station station) {
        validateExistsInSections(station);
        validateMinSectionSize();

        List<Section> relatedSections = this.values.stream()
            .filter(value -> value.containsStation(station))
            .collect(Collectors.toUnmodifiableList());

        if (relatedSections.size() == 1) {
            this.values.remove(relatedSections.get(0));
            return new SectionRemovalResult(null, List.of(relatedSections.get(0)));
        }

        return removeMiddleStation(relatedSections);
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

    private SectionRemovalResult removeMiddleStation(List<Section> relatedSections) {
        Section matchedSection = relatedSections.get(0);
        Section nextSection = relatedSections.get(1);

        Section connectedSection = matchedSection.removeMiddleStation(nextSection);
        this.values.add(this.values.indexOf(matchedSection), connectedSection);
        this.values.remove(matchedSection);
        this.values.remove(nextSection);

        List<Section> sectionToRemove = List.of(matchedSection, nextSection);
        return new SectionRemovalResult(connectedSection, sectionToRemove);
    }

    public List<Station> getStations() {
        List<Station> stations = toMappedList(unmodifiableList(values), Section::getUpStation);
        stations.add(getLast().getDownStation());
        return unmodifiableList(stations);
    }

    private Section getLast() {
        return this.values.get(this.values.size() - 1);
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
