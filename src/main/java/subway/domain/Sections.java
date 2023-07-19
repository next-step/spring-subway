package subway.domain;

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

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void canDeleteStation(Long stationId) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("구간이 1개 이하이므로 해당역을 삭제할 수 없습니다.");
        }
        Station endStation = findEndStation();
        if (endStation.getId() != stationId) {
            throw new IllegalArgumentException("하행 종점역이 아니면 삭제할 수 없습니다.");
        }
    }

    private Station findEndStation() {
        Set<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());

        Station station = sections.stream()
            .map(Section::getDownStation)
            .filter(downStation -> !upStations.contains(downStation))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
        return station;
    }

    public List<Station> sortStations() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Section> stationLayerMap = initLayerMap();

        Station nowStation = findTopStation();
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

    private Station findTopStation() {
        return findStations().stream()
            .filter(station -> !findEndStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
    }

    public SectionRegistVo registSection(Section section) {
        if (sections.size() == 0) {
            return new SectionRegistVo(section);
        }
        if (findStations().contains(section.getUpStation()) && findStations().contains(
            section.getDownStation())) {
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

    private SectionRegistVo registUpSection(Section section) {
        if (!findStartStations().contains(section.getUpStation())) {
            return new SectionRegistVo(section);
        }

        return registMiddleUpSection(section);
    }

    private SectionRegistVo registMiddleUpSection(Section section) {
        int distance = section.getDistance().getDistance();
        Section duplicatedUpSection = findSectionByUpStation(section.getUpStation());

        validateDistance(distance, duplicatedUpSection);

        Section modifySection = new Section(
            duplicatedUpSection.getId(),
            section.getDownStation(),
            duplicatedUpSection.getDownStation(),
            section.getLine(),
            duplicatedUpSection.getDistance().getDistance() - distance
        );
        return new SectionRegistVo(section, modifySection);
    }

    private SectionRegistVo registDownSection(Section section) {
        if (!findEndStations().contains(section.getDownStation())) {
            return new SectionRegistVo(section);
        }

        return registMiddleDownSection(section);
    }

    private SectionRegistVo registMiddleDownSection(Section section) {
        int distance = section.getDistance().getDistance();
        Section duplicatedDownSection = findSectionByDownStation(section.getDownStation());

        validateDistance(distance, duplicatedDownSection);

        Section modifySection = new Section(
            duplicatedDownSection.getId(),
            duplicatedDownSection.getUpStation(),
            section.getUpStation(),
            section.getLine(),
            duplicatedDownSection.getDistance().getDistance() - distance
        );
        return new SectionRegistVo(section, modifySection);
    }

    private void validateDistance(int distance, Section duplicatedUpSection) {
        if (distance >= duplicatedUpSection.getDistance().getDistance()) {
            throw new IllegalArgumentException("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
        }
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
    }

    private Set<Station> findStations() {
        return Stream.concat(
                findStartStations().stream(),
                findEndStations().stream()
            )
            .collect(Collectors.toSet());
    }

    private Set<Station> findStartStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
    }

    private Set<Station> findEndStations() {
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
