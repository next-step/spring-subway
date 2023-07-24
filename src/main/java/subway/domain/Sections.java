package subway.domain;

import java.text.MessageFormat;
import java.util.*;
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
            .orElseThrow(() -> new IllegalStateException("현재 노선의 역 정보가 올바르지 않습니다."));
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

        return Collections.unmodifiableList(sortedStations);
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
            .orElseThrow(() -> new IllegalStateException("현재 노선의 역 정보가 올바르지 않습니다."));
    }

    public SectionRegisterVo registerSection(Section section) {
        validRegisterSection(section);
        if (findStations().contains(section.getUpStation())) {
            return registerUpSection(section);
        }
        if (findStations().contains(section.getDownStation())) {
            return registerDownSection(section);
        }
        return new SectionRegisterVo(section);
    }

    private void validRegisterSection(Section section) {
        if (findStations().contains(section.getUpStation())
            && findStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
        if (!findStations().contains(section.getUpStation())
                && !findStations().contains(section.getDownStation())
                && !sections.isEmpty()) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 2개의 역이 모두 노선에 포함되지 않아 추가할 수 없습니다.");
        }
    }

    private SectionRegisterVo registerUpSection(Section registerSection) {
        if (!findStartStations().contains(registerSection.getUpStation())) {
            return new SectionRegisterVo(registerSection);
        }
        return registerMiddleUpSection(registerSection);
    }

    private SectionRegisterVo registerMiddleUpSection(Section registerSection) {
        Section duplicatedUpSection = findSectionByUpStation(registerSection.getUpStation());
        return registerSection.makeNewUpSection(duplicatedUpSection);
    }

    private SectionRegisterVo registerDownSection(Section registerSection) {
        if (!findEndStations().contains(registerSection.getDownStation())) {
            return new SectionRegisterVo(registerSection);
        }
        return registerMiddleDownSection(registerSection);
    }

    private SectionRegisterVo registerMiddleDownSection(Section registerSection) {
        Section duplicatedDownSection = findSectionByDownStation(registerSection.getDownStation());
        return registerSection.makeNewDownSection(duplicatedDownSection);
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
