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
import subway.domain.vo.SectionRegisterVo;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
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

        if (sectionsLinesCount > 1L) {
            throw new IllegalArgumentException("서로 다른 호선의 구간이 들어가 있습니다.");
        }
    }

    public void canDeleteStation(Long stationId) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                MessageFormat.format("구간이 {0}개 이하이므로 해당역을 삭제할 수 없습니다.", MINIMUM_SIZE)
            );
        }
        Station endStation = findEndStation();
        if (!Objects.equals(endStation.getId(), stationId)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 삭제할 수 없습니다.");
        }
    }

    private Station findEndStation() {
        return findStations().stream()
            .filter(station -> !findStartStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
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

    public SectionRegisterVo registerSection(Section section) {
        if (sections.size() == 0) {
            return new SectionRegisterVo(section);
        }
        if (findStations().contains(section.getUpStation())
            && findStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
        if (findStations().contains(section.getUpStation())) {
            return registerUpSection(section);
        }
        if (findStations().contains(section.getDownStation())) {
            return registerDownSection(section);
        }
        throw new IllegalArgumentException("해당 구간은 추가할 수 없습니다.");
    }

    private SectionRegisterVo registerUpSection(Section registerSection) {
        if (!findStartStations().contains(registerSection.getUpStation())) {
            return new SectionRegisterVo(registerSection);
        }
        return registerMiddleUpSection(registerSection);
    }

    private SectionRegisterVo registerMiddleUpSection(Section registerSection) {
        Distance distance = registerSection.getDistance();
        Section duplicatedUpSection = findSectionByUpStation(registerSection.getUpStation());

        validateDistance(distance, duplicatedUpSection);

        Section modifySection = new Section(
            duplicatedUpSection.getId(),
            registerSection.getDownStation(),
            duplicatedUpSection.getDownStation(),
            registerSection.getLine(),
            duplicatedUpSection.getDistance().subtract(distance)
        );

        return new SectionRegisterVo(registerSection, modifySection);
    }

    private SectionRegisterVo registerDownSection(Section registerSection) {
        if (!findEndStations().contains(registerSection.getDownStation())) {
            return new SectionRegisterVo(registerSection);
        }
        return registerMiddleDownSection(registerSection);
    }

    private SectionRegisterVo registerMiddleDownSection(Section registerSection) {
        Distance distance = registerSection.getDistance();
        Section duplicatedDownSection = findSectionByDownStation(registerSection.getDownStation());

        validateDistance(distance, duplicatedDownSection);

        Section modifySection = new Section(
            duplicatedDownSection.getId(),
            duplicatedDownSection.getUpStation(),
            registerSection.getUpStation(),
            registerSection.getLine(),
            duplicatedDownSection.getDistance().subtract(distance)
        );

        return new SectionRegisterVo(registerSection, modifySection);
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
