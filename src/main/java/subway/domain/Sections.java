package subway.domain;

import org.springframework.util.Assert;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Sections {
    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        Assert.isTrue(sections.size() >= MINIMUM_SIZE, "노선에 등록된 구간은 반드시 한개 이상이어야합니다.");
        this.sections = sort(sections);
    }

    private List<Section> sort(final List<Section> sections) {
        if (sections.isEmpty()) {
            return List.of();
        }
        final Map<Station, Section> upStationMap = initializeUpStationMap(
                sections);
        final Map<Station, Section> downStationMap = initializeDownStationMap(
                sections);

        final Section firstSection = findFirstSection(sections, downStationMap);
        return sortedSections(upStationMap, firstSection);
    }

    private Map<Station, Section> initializeUpStationMap(final List<Section> sections) {
        final Map<Station, Section> upStationMap = new HashMap<>();
        for (Section section : sections) {
            upStationMap.put(section.getUpStation(), section);
        }
        return upStationMap;
    }

    private Map<Station, Section> initializeDownStationMap(final List<Section> sections) {
        final Map<Station, Section> downStationMap = new HashMap<>();
        for (Section section : sections) {
            downStationMap.put(section.getDownStation(), section);
        }
        return downStationMap;
    }

    private Section findFirstSection(final List<Section> sections,
                                     final Map<Station, Section> downStationMap) {
        Section pivot = sections.get(0);
        while (downStationMap.containsKey(pivot.getUpStation())) {
            pivot = downStationMap.get(pivot.getUpStation());
        }
        return pivot;
    }

    private List<Section> sortedSections(final Map<Station, Section> upStationMap,
                                         Section pivot) {
        List<Section> result = new ArrayList<>();
        result.add(pivot);
        while (upStationMap.containsKey(pivot.getDownStation())) {
            pivot = upStationMap.get(pivot.getDownStation());
            result.add(pivot);
        }
        return result;
    }

    public List<Station> toStations() {
        if (sections.isEmpty()) {
            return List.of();
        }
        final List<Station> result = sections.stream()
                .map(Section::getUpStation)
                .collect(toList());
        result.add(sections.get(sections.size() - 1).getDownStation());
        return result;
    }

    public int sectionLength() {
        return sections.size();
    }

    public Section deleteLastSection() {
        if (sectionLength() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
        final Section lastSection = findLastSection();
        sections.remove(lastSection);
        return lastSection;
    }

    public boolean isLastDownStation(final Station station) {
        return findLastSection().getDownStation().equals(station);
    }

    private Section findLastSection() {
        return sections.get(sectionLength() - 1);
    }

    public void addSection(Section newSection) {
        final List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(toList());
        final List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(toList());
        throwIfAllOrNothingMatchInLine(newSection);
        addIfMatchUpStation(newSection, upStations);
        addIfMatchDownStation(newSection, downStations);
    }

    private void throwIfAllOrNothingMatchInLine(final Section newSection) {
        final List<Station> stations = toStations();
        if (stations.contains(newSection.getUpStation()) == stations.contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
        }
    }

    private void addIfMatchDownStation(final Section newSection, final List<Station> stations) {
        if (stations.contains(newSection.getDownStation())) {
            final Section originSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            final Line line = newSection.getLine();
            final Station upstation = originSection.getUpStation();
            final Station downStation = newSection.getUpStation();
            final long distance = originSection.getDistance() - newSection.getDistance();
            sections.remove(originSection);
            sections.add(newSection);
            sections.add(new Section(originSection.getId(), line, upstation, downStation, new Distance(distance)));
        }
        if (stations.contains(newSection.getUpStation())) {
            sections.add(newSection);
        }
    }

    private void addIfMatchUpStation(final Section newSection, final List<Station> stations) {
        if (stations.contains(newSection.getUpStation())) {
            final Section originSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            final Line line = newSection.getLine();
            final Station upstation = newSection.getDownStation();
            final Station downStation = originSection.getDownStation();
            final long distance = originSection.getDistance() - newSection.getDistance();
            sections.remove(originSection);
            sections.add(newSection);
            sections.add(new Section(originSection.getId(), line, upstation, downStation, new Distance(distance)));
        }
        if (stations.contains(newSection.getDownStation())) {
            sections.add(newSection);
        }
    }


    public List<Section> getSections() {
        return sections.stream()
                .map(Section::new)
                .collect(toUnmodifiableList());
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
