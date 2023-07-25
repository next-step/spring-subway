package subway.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private static final Long UNIQUE_LINE_COUNT = 1L;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validSections(sections);
        this.sections = sections;
    }

    private void validSections(List<Section> sections) {
        long sectionsLinesCount = sections.stream()
            .map(Section::getLine)
            .distinct()
            .count();

        if (sectionsLinesCount > UNIQUE_LINE_COUNT) {
            throw new IllegalArgumentException("서로 다른 호선의 구간이 들어가 있습니다.");
        }
    }

    public void canDeleteStation(Station deleteStation) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                MessageFormat.format("구간이 {0}개 이하이므로 해당역을 삭제할 수 없습니다.", MINIMUM_SIZE)
            );
        }
        if (!findStations().contains(deleteStation)) {
            throw new IllegalArgumentException("해당역이 없어 삭제할 수 없습니다.");
        }
    }

    public Optional<Section> findUpSectionFrom(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst();
    }

    public Optional<Section> findDownSectionFrom(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst();
    }

    public List<Station> sortStations() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Section> stationLayerMap = initLayerMap();

        Station nowStation = findUpTerminusStation();
        sortedStations.add(nowStation);

        while (stationLayerMap.containsKey(nowStation)) {
            Section section = stationLayerMap.get(nowStation);
            sortedStations.add(section.getDownStation());
            nowStation = section.getDownStation();
        }

        return sortedStations;
    }

    private Map<Station, Section> initLayerMap() {
        return sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Function.identity()
            ));
    }

    public Optional<Section> findModifiedSection(Section section) {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        validExistedStation(section);
        validDuplicatedStation(section);
        Optional<Section> targetSection = findDuplicatedSection(section);
        if (targetSection.isEmpty()) {
            return Optional.empty();
        }
        return divideSection(section, targetSection);
    }

    private void validExistedStation(Section section) {
        if (!matchUpStation(section) && !matchDownStation(section)) {
            throw new IllegalArgumentException("해당 구간은 추가할 수 없습니다.");
        }
    }

    private void validDuplicatedStation(Section section) {
        if (matchUpStation(section) && matchDownStation(section)) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
    }

    private Optional<Section> findDuplicatedSection(Section section) {
        return sections.stream()
            .map(section::findDuplicatedSection)
            .filter(Objects::nonNull)
            .findFirst();
    }

    private Optional<Section> divideSection(Section section, Optional<Section> targetSection) {
        if (matchUpStation(section)) {
            return Optional.of(section.divideDownSection(targetSection.get()));
        }
        if (matchDownStation(section)) {
            return Optional.of(section.divideUpSection(targetSection.get()));
        }
        return Optional.empty();
    }

    private boolean matchUpStation(Section section) {
        return findStations().contains(section.getUpStation());
    }

    private boolean matchDownStation(Section section) {
        return findStations().contains(section.getDownStation());
    }

    private Station findUpTerminusStation() {
        return findStations().stream()
            .filter(station -> !findDownStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
    }

    private Set<Station> findStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Station> findDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "sections=" + sections +
            '}';
    }

}
