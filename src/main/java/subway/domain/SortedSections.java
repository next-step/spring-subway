package subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SortedSections extends Sections {
    public SortedSections(final List<Section> sections) {
        super(sections);
        this.sections = sort(sections);
    }

    @Override
    public List<Station> toStations() {
        final List<Station> result = sections.stream()
                .map(Section::getUpStation)
                .collect(toList());
        result.add(findLastSection().getDownStation());
        return result;
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
        Section firstSection = sections.get(0);
        while (downStationMap.containsKey(firstSection.getUpStation())) {
            firstSection = downStationMap.get(firstSection.getUpStation());
        }
        return firstSection;
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
}
