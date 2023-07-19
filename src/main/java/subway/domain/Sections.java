package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
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

    private List<Section> sort(List<Section> sections) {
        if (sections.isEmpty()) {
            return List.of();
        }
        Section pivot = getFirstSection(sections);
        return getSortedSections(sections, pivot);
    }

    private Section getFirstSection(List<Section> sections) {
        Section pivot = sections.get(0);
        while (true) {
            Optional<Section> temp = findUpSection(sections, pivot);
            if (temp.isEmpty()) {
                return pivot;
            }
            pivot = temp.get();
        }
    }

    private List<Section> getSortedSections(List<Section> sections, Section pivot) {
        List<Section> result = new ArrayList<>();
        while (true) {
            result.add(pivot);
            Optional<Section> temp = findDownSection(sections, pivot);
            if (temp.isEmpty()) {
                return result;
            }
            pivot = temp.get();
        }
    }

    private Optional<Section> findUpSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(pivot.getUpStationId()))
                .findAny();
    }

    private Optional<Section> findDownSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(pivot.getDownStationId()))
                .findAny();
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
