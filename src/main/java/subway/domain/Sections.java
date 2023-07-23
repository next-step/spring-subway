package subway.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public void canDeleteStation(Long stationId) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                MessageFormat.format("구간이 {0}개 이하이므로 해당역을 삭제할 수 없습니다.", MINIMUM_SIZE)
            );
        }
        Station downPointStation = findDownPointStation();
        if (!Objects.equals(downPointStation.getId(), stationId)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 삭제할 수 없습니다.");
        }
    }

    private Station findDownPointStation() {
        return findStations().stream()
            .filter(station -> !findUpStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
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

    private Station findUpPointStation() {
        return findStations().stream()
            .filter(station -> !findDownStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
    }

    public SectionRegistVo registSection(Section section) {
        if (sections.isEmpty()) {
            return new SectionRegistVo(section);
        }
        if (findStations().contains(section.getUpStation())
            && findStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
        if (findStations().contains(section.getUpStation())) {
            return registUpSection(section);
        }
        if (findStations().contains(section.getDownStation())) {
            return registDownSection(section);
        }
        throw new IllegalArgumentException("해당 구간은 추가할 수 없습니다.");
    }

    private SectionRegistVo registUpSection(Section registSection) {
        if (!findStartStations().contains(registSection.getUpStation())) {
            return new SectionRegistVo(registSection);
        }
        return registMiddleUpSection(registSection);
    }

    private SectionRegistVo registMiddleUpSection(Section registSection) {
        Distance distance = registSection.getDistance();
        Section duplicatedUpSection = findSectionByUpStation(registSection.getUpStation());

        validateDistance(distance, duplicatedUpSection);

        Section modifySection = new Section(
            duplicatedUpSection.getId(),
            registSection.getDownStation(),
            duplicatedUpSection.getDownStation(),
            registSection.getLine(),
            duplicatedUpSection.getDistance().subtract(distance)
        );

        return new SectionRegistVo(registSection, modifySection);
    }

    private SectionRegistVo registDownSection(Section registSection) {
        if (!findDownStations().contains(registSection.getDownStation())) {
            return new SectionRegistVo(registSection);
        }
        return registMiddleDownSection(registSection);
    }

    private SectionRegistVo registMiddleDownSection(Section registSection) {
        Distance distance = registSection.getDistance();
        Section duplicatedDownSection = findSectionByDownStation(registSection.getDownStation());

        validateDistance(distance, duplicatedDownSection);

        Section modifySection = new Section(
            duplicatedDownSection.getId(),
            duplicatedDownSection.getUpStation(),
            registSection.getUpStation(),
            registSection.getLine(),
            duplicatedDownSection.getDistance().subtract(distance)
        );

        return new SectionRegistVo(registSection, modifySection);
    }

    private void validateDistance(Distance distance, Section duplicatedUpSection) {
        if (duplicatedUpSection.isOverDistance(distance)) {
            throw new IllegalArgumentException("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
        }
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.downStationEquals(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.upStationEquals(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
    }

    private Set<Station> findStations() {
        return Stream.concat(
                findUpStations().stream(),
                findDownStations().stream()
            )
            .collect(Collectors.toSet());
    }

    public Set<Station> findUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
    }

    private Set<Station> findDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
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
