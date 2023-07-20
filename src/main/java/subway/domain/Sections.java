package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.vo.SectionAdditionResult;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = sort(values);

        validateConnectedSections(values, this.values);
        validateSectionsOfSameLine(values);
    }

    private void validateConnectedSections(List<Section> values, List<Section> sortedValues) {
        if (sortedValues.size() != values.size()) {
            throw new IllegalArgumentException("끊어진 구간을 입력했습니다.");
        }
    }

    private void validateSectionsOfSameLine(List<Section> values) {
        if (isAllBelongToSameLine(values)) {
            throw new IllegalArgumentException("다른 노선의 구간을 입력했습니다");
        }
    }

    private boolean isAllBelongToSameLine(List<Section> values) {
        return values.stream()
            .anyMatch(value -> !getLast().belongToSameLine(value));
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

    public SectionAdditionResult add(Section section) {
        //해당 섹션이 같인 라인인지 확인한다
        this.validateSectionsBelongToLine(section.getLine());
        // 특정 스테이션이 이 섹션즈 안에 포함되는지
        validateOneInOneOut(section);

        // 상이나 하 중에 하나 맞을 떄
        Optional<Section> foundSectionOptional = values.stream()
            .filter(value -> value.hasSameUpStationOrDownStation(section))
            .findAny();

        if (foundSectionOptional.isPresent()) {
            Section foundSection = foundSectionOptional.get();
            List<Section> sectionsToAdd = foundSection.mergeSections(section);
            //replace
            int index = values.indexOf(foundSection);
            this.values.remove(index);
            this.values.add(index, sectionsToAdd.get(1));
            this.values.add(index, sectionsToAdd.get(0));

            return new SectionAdditionResult(Optional.of(foundSection), sectionsToAdd);
        }

        // 맨앞일 때
        if (section.canPrecede(getFirst())) {
            addFirst(section);
            return new SectionAdditionResult(Optional.empty(), List.of(getFirst()));
        }

        //맨 뒤일때
        if (getLast().canPrecede(section)) {
            addLast(section);
            return new SectionAdditionResult(Optional.empty(), List.of(getLast()));
        }

        throw new IllegalStateException("여기 오면 안되는데?");
    }

    private void validateOneInOneOut(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        boolean upStationExists = this.values.stream().anyMatch(value -> value.containsStation(upStation));
        boolean downStationExists = this.values.stream().anyMatch(value -> value.containsStation(downStation));

        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있습니다.");
        }
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("두 역이 모두 노선에 포함되어 있지 않습니다.");
        }
    }

    private void addFirst(Section section) {
        this.values.add(0, section);
    }

    public Section addLast(Section section) {
        validateAddable(section);
        validateNotContainDownStationOf(section);
        this.values.add(section);
        return section;
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
        if (values.size() <= 1) {
            throw new IllegalStateException("노선의 구간이 1개인 경우 삭제할 수 없습니다.");
        }
    }

    void validateSectionsBelongToLine(Line line) {
        if (!this.getLast().belongTo(line)) {
            throw new IllegalArgumentException(
                "현재 구간은 해당 노선에 속하지 않습니다. current line: " + line + ", section: " + this.getLast());
        }
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
