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
import subway.domain.vo.SectionRegistVo;

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

    public Optional<Section> findUpSection(Station deleteStation) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(deleteStation))
            .findFirst();
    }

    public Optional<Section> findDownSection(Station deleteStation) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(deleteStation))
            .findFirst();
    }

    public List<Station> sortStations() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Section> stationLayerMap = initLayerMap();

        Station nowStation = findUpPointStation();
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

    public SectionRegistVo registSection(Section newSection) {
        if (sections.isEmpty()) {
            return new SectionRegistVo(newSection);
        }
        validRegistSection(newSection);

        Optional<Section> targetSection = findTargetSection(newSection);
        if (targetSection.isEmpty()) {
            return new SectionRegistVo(newSection);
        }
        return registMiddleSection(newSection, targetSection.get());
    }

    private void validRegistSection(Section newSection) {
        validDuplicatedStation(newSection);
        validExistedStation(newSection);
    }

    private void validExistedStation(Section newSection) {
        if (!isMatchUpStation(newSection) && !isMatchDownStation(newSection)) {
            throw new IllegalArgumentException("해당 구간은 추가할 수 없습니다.");
        }
    }

    private void validDuplicatedStation(Section newSection) {
        if (isMatchUpStation(newSection) && isMatchDownStation(newSection)) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
    }


    private SectionRegistVo registMiddleSection(Section newSection, Section targetSection) {
        if (isMatchUpStation(newSection)) {
            return new SectionRegistVo(newSection, newSection.findMiddleUpSection(targetSection));
        }
        if (isMatchDownStation(newSection)) {
            return new SectionRegistVo(newSection, newSection.findMiddleDownSection(targetSection));
        }

        throw new IllegalArgumentException("해당 구간은 추가할 수 없습니다.");
    }

    private Optional<Section> findTargetSection(Section newSection) {
        return sections.stream()
            .map(newSection::targetSection)
            .filter(Objects::nonNull)
            .findFirst();
    }

    private boolean isMatchUpStation(Section newSection) {
        return findStations().contains(newSection.getUpStation());
    }

    private boolean isMatchDownStation(Section newSection) {
        return findStations().contains(newSection.getDownStation());
    }

    private Station findUpPointStation() {
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
