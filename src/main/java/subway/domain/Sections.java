package subway.domain;

import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
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
