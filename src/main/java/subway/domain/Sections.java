package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validNewSection(Section section) {
        if (sections.size() == 0) {
            return;
        }
        validNewSectionDownStation(section);
        validNewSectionUpStation(section);
    }

    private void validNewSectionDownStation(Section section) {
        sections.stream()
            .filter(s -> !s.getUpStation().equals(section.getDownStation()))
            .filter(s -> !s.getDownStation().equals(section.getDownStation()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("입력하고자 하는 하행역이 해당 노선에 등록되어 있습니다.")
            );
    }

    private void validNewSectionUpStation(Section section) {
        Station endStation = findEndStation();
        if (!endStation.equals(section.getUpStation())) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.");
        }
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

    public List<Section> registSection(Section section) {
        if (sections.size() == 0) {
            return List.of(section);
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

    private List<Section> registUpSection(Section section) {
        if (!findStartStations().contains(section.getUpStation())) {
            return List.of(section);
        }

        return registMiddleUpSection(section);
    }

    private List<Section> registDownSection(Section section) {
        if (!findEndStations().contains(section.getDownStation())) {
            return List.of(section);
        }

        return registMiddleDownSection(section);
    }

    private List<Section> registMiddleUpSection(Section section) {
        int distance = section.getDistance().getDistance();
        Section duplicatedUpSection = findSectionByUpStation(section.getUpStation());

        validateDistance(distance, duplicatedUpSection);

        List<Section> sections = new ArrayList<>(List.of(section));
        Section modifySection = new Section(
            duplicatedUpSection.getId(),
            section.getDownStation(),
            duplicatedUpSection.getDownStation(),
            section.getLine(),
            duplicatedUpSection.getDistance().getDistance() - distance
        );
        sections.add(modifySection);
        return sections;
    }

    private List<Section> registMiddleDownSection(Section section) {
        int distance = section.getDistance().getDistance();
        Section duplicatedDownSection = findSectionByDownStation(section.getDownStation());

        validateDistance(distance, duplicatedDownSection);

        List<Section> sections = new ArrayList<>(List.of(section));
        Section modifySection = new Section(
            duplicatedDownSection.getId(),
            duplicatedDownSection.getUpStation(),
            section.getUpStation(),
            section.getLine(),
            duplicatedDownSection.getDistance().getDistance() - distance
        );
        sections.add(modifySection);
        return sections;
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
