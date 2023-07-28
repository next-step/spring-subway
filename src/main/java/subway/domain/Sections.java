package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        if (sections.isEmpty()) {
            return List.of();
        }

        Map<Station, Section> upStationMap = sections.stream().collect(Collectors.toMap(
                Section::getUpStation,
                Function.identity()
        ));
        Map<Station, Section> downStationMap = sections.stream().collect(Collectors.toMap(
                Section::getDownStation,
                Function.identity()
        ));

        Section firstSection = findFirstSection(sections, downStationMap);
        return sortedSections(upStationMap, firstSection);
    }

    private static Section findFirstSection(
            List<Section> sections,
            Map<Station, Section> downStationMap) {

        Section pivot = sections.get(0);
        while (downStationMap.containsKey(pivot.getUpStation())) {
            pivot = downStationMap.get(pivot.getUpStation());
        }
        return pivot;
    }

    private static List<Section> sortedSections(
            Map<Station, Section> upStationMap,
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

    public boolean isFirstStation(Station downStation) {
        return !sections.isEmpty() &&
                sections.get(0).getUpStation().equals(downStation);
    }

    public boolean isLastStation(Station upStation) {
        return !sections.isEmpty() &&
                sections.get(sections.size() - 1).getDownStation().equals(upStation);
    }

    public Optional<Section> findByUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)).findAny();
    }

    public Optional<Section> findByDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId)).findAny();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Section> findDeleteSections(Long stationId) {
        Optional<Section> upSection = findByDownStationId(stationId);
        Optional<Section> downSection = findByUpStationId(stationId);
        SectionDeleteType deleteType = SectionDeleteType.of(
                sections.size(),
                upSection.isPresent(),
                downSection.isPresent());

        return deleteType.findDeleteSections(upSection.orElse(null), downSection.orElse(null));
    }

    public Optional<Section> findCombinedSection(Long stationId) {
        Optional<Section> upSection = findByDownStationId(stationId);
        Optional<Section> downSection = findByUpStationId(stationId);
        SectionDeleteType deleteType = SectionDeleteType.of(
                sections.size(),
                upSection.isPresent(),
                downSection.isPresent());

        return deleteType.findCombinedSection(upSection.orElse(null), downSection.orElse(null));
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
