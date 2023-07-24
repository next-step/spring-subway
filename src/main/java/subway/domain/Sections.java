package subway.domain;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import subway.domain.vo.SectionsRegister;

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

    public void validDeleteStation() {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                    MessageFormat.format("구간이 {0}개 이하이므로 해당역을 삭제할 수 없습니다.", MINIMUM_SIZE)
            );
        }
    }

    private Station findEndStation() {
        return findStations().stream()
            .filter(station -> !findUpStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 노선의 역 정보가 올바르지 않습니다."));
    }

    public List<Station> sortStations() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Section> sectionMapByUpStation = initSectionMapByUpStation();

        Station nowStation = findTopStation();
        sortedStations.add(nowStation);

        while (sectionMapByUpStation.containsKey(nowStation)) {
            Section section = sectionMapByUpStation.get(nowStation);
            sortedStations.add(section.getDownStation());
            nowStation = section.getDownStation();
        }

        return Collections.unmodifiableList(sortedStations);
    }

    private Map<Station, Section> initSectionMapByUpStation() {
        return sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Function.identity()
            ));
    }

    private Station findTopStation() {
        return findStations().stream()
            .filter(station -> !findDownStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 노선의 역 정보가 올바르지 않습니다."));
    }

    public SectionsRegister registerSection(Section section) {
        validRegisterSection(section);
        if (findStations().contains(section.getUpStation())) {
            return registerDownStation(section);
        }
        if (findStations().contains(section.getDownStation())) {
            return registerUpStation(section);
        }
        return new SectionsRegister(section);
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

    private SectionsRegister registerDownStation(Section registerSection) {
        if (!findUpStations().contains(registerSection.getUpStation())) {
            return new SectionsRegister(registerSection);
        }
        return registerNewSectionByUpStation(registerSection);
    }

    private SectionsRegister registerNewSectionByUpStation(Section registerSection) {
        Section duplicatedUpSection = findSectionByUpStation(registerSection.getUpStation())
                .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
        return new SectionsRegister(registerSection, registerSection.makeNewUpSection(duplicatedUpSection));
    }

    private SectionsRegister registerUpStation(Section registerSection) {
        if (!findDownStations().contains(registerSection.getDownStation())) {
            return new SectionsRegister(registerSection);
        }
        return registerNewSectionByDownStation(registerSection);
    }

    private SectionsRegister registerNewSectionByDownStation(Section registerSection) {
        Section duplicatedDownSection = findSectionByDownStation(registerSection.getDownStation())
                .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
        return new SectionsRegister(registerSection, registerSection.makeNewDownSection(duplicatedDownSection));
    }

    public Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.downStationEquals(station))
            .findFirst();
    }

    public Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.upStationEquals(station))
            .findFirst();
    }

    private Set<Station> findStations() {
        Set<Station> stations = new HashSet<>();
        stations.addAll(findUpStations());
        stations.addAll(findDownStations());
        return stations;
    }

    private Set<Station> findUpStations() {
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
