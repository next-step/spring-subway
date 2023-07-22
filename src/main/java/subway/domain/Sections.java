package subway.domain;

import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        Assert.isTrue(sections.size() >= MINIMUM_SIZE, "노선에 등록된 구간은 반드시 한개 이상이어야합니다.");
        this.sections = sort(sections);
    }

    private Map<Station, Section> initializeUpStationMap(List<Section> sections) {
        Map<Station, Section> upStationMap = new HashMap<>();
        for (Section section : sections) {
            upStationMap.put(section.getUpStation(), section);
        }
        return upStationMap;
    }

    private Map<Station, Section> initializeDownStationMap(List<Section> sections) {
        Map<Station, Section> downStationMap = new HashMap<>();
        for (Section section : sections) {
            downStationMap.put(section.getDownStation(), section);
        }
        return downStationMap;
    }

    private Section findFirstSection(List<Section> sections,
                                     Map<Station, Section> downStationMap) {
        Section pivot = sections.get(0);
        while (downStationMap.containsKey(pivot.getUpStation())) {
            pivot = downStationMap.get(pivot.getUpStation());
        }
        return pivot;
    }

    private List<Section> sortedSections(Map<Station, Section> upStationMap,
                                         Section pivot) {
        List<Section> result = new ArrayList<>();
        result.add(pivot);
        while (upStationMap.containsKey(pivot.getDownStation())) {
            pivot = upStationMap.get(pivot.getDownStation());
            result.add(pivot);
        }
        return result;
    }

    private List<Section> sort(List<Section> sections) {
        if (sections.isEmpty()) {
            return List.of();
        }

        Map<Station, Section> upStationMap = initializeUpStationMap(
                sections);

        Map<Station, Section> downStationMap = initializeDownStationMap(
                sections);

        Section firstSection = findFirstSection(sections, downStationMap);

        return sortedSections(upStationMap, firstSection);
    }

    public List<Station> toStations() {
        if (sections.isEmpty()) {
            return List.of();
        }
        List<Station> result = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        result.add(sections.get(sections.size() - 1).getDownStation());
        return result;
    }

    public Section findLastSection() {
        return sections.get(sectionLength() - 1);
    }

    public int sectionLength() {
        return sections.size();
    }

    public Section deleteLastSection() {
        if (sectionLength() <= MINIMUM_SIZE) {
            throw new IllegalStateException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
        final Section lastSection = findLastSection();
        sections.remove(lastSection);
        return lastSection;
    }

    public boolean isLastDownStation(Station station) {
        return findLastSection().getDownStation().equals(station);
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
