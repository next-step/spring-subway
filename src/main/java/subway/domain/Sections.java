package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import subway.domain.vo.SectionDeleteVo;
import subway.exception.SectionCreateException;
import subway.exception.SectionDeleteException;

public class Sections {

    private static final int MINIMUM_DELETE_SIZE = 2;
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

    public Optional<Section> findDownStationMatchSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation)).findAny();
    }

    public Optional<Section> findUpStationMatchSection(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation)).findAny();
    }

    private static Optional<Section> cutSection(Station upStation, Station downStation,
            Distance distance, Section originalSection) {
        return Optional.of(originalSection
                .cuttedSection(upStation, downStation, distance));
    }

    private static void validateStationBothExist(boolean upStationSection,
            boolean downStationSection, boolean isLast, boolean isFirst) {
        if ((upStationSection || isLast)
                && (downStationSection || isFirst)) {
            throw new SectionCreateException("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
        }
    }

    private static void validateStationNotExist(boolean upStationSection,
            boolean downStationSection, boolean isLast, boolean isFirst) {
        if (upStationSection && downStationSection && !isLast && !isFirst) {
            throw new SectionCreateException("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
        }
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public SectionDeleteVo findDeletedAndCombinedSections(Long stationId) {
        if (sections.isEmpty()) {
            throw new SectionDeleteException("노선에 존재하는 역이 없습니다.");
        }
        if (sections.size() < MINIMUM_DELETE_SIZE) {
            throw new SectionDeleteException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
        Optional<Section> upSection = sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findAny();
        Optional<Section> downSection = sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findAny();

        if (upSection.isPresent() && downSection.isPresent()) {
            List<Section> deleteSections = List.of(upSection.get(), downSection.get());
            Section combinedSection = upSection.get().combine(downSection.get());
            return new SectionDeleteVo(deleteSections, Optional.of(combinedSection));
        }
        if (upSection.isPresent()) {
            List<Section> deleteSections = List.of(upSection.get());
            return new SectionDeleteVo(deleteSections, Optional.empty());
        }
        if (downSection.isPresent()) {
            List<Section> deleteSections = List.of(downSection.get());
            return new SectionDeleteVo(deleteSections, Optional.empty());
        }

        throw new SectionDeleteException("노선에 해당하는 역을 가진 구간이 없습니다.");
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
