package subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
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

    private static Map<Station, Section> initializeUpStationMap(List<Section> sections) {
        Map<Station, Section> upStationMap = new HashMap<>();
        for (Section section : sections) {
            upStationMap.put(section.getUpStation(), section);
        }
        return upStationMap;
    }

    private static Map<Station, Section> initializeDownStationMap(List<Section> sections) {
        Map<Station, Section> downStationMap = new HashMap<>();
        for (Section section : sections) {
            downStationMap.put(section.getDownStation(), section);
        }
        return downStationMap;
    }

    private static Section findFirstSection(List<Section> sections,
            Map<Station, Section> downStationMap) {
        Section pivot = sections.get(0);
        while (downStationMap.containsKey(pivot.getUpStation())) {
            pivot = downStationMap.get(pivot.getUpStation());
        }
        return pivot;
    }

    private static List<Section> sortedSections(Map<Station, Section> upStationMap,
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
        List<Station> result = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        result.add(sections.get(sections.size() - 1).getDownStation());
        return result;
    }

    public Section findLastSection() {
        validateSize(sections);
        return sections.get(sections.size() - 1);
    }

    private static void validateSize(List<Section> sections) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
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
