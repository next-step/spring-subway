package subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역은 기존 하행 종점역과 같아야 합니다.";
    private static final String DUPLICATED_EXCEPTION_MESSAGE = "새로운 구간의 하행역은 기존 노선에 등록되어 있지 않은 역이어야 합니다.";
    private static final String EMPTY_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sorted(sections);
    }

    public void insert(final Section newSection) {
        validateSameStation(newSection);
        validateDuplicated(newSection);

        sections.add(newSection);
    }

    public void delete(Station lastStation) {
        validateEmpty();
        if (this.sections.size() <= 1) {
            throw new IllegalStateException("구간은 0개가 될 수 없습니다.");
        }
        int lastIndex = this.sections.size() - 1;
        if (!this.sections.get(lastIndex).getDownStation().equals(lastStation)) {
            throw new IllegalArgumentException("마지막 구간만 삭제할 수 있습니다.");
        }
        this.sections.remove(lastIndex);
    }

    private List<Section> sorted(final List<Section> sections) {
        if (sections.isEmpty()) {
            return sections;
        }

        Station first = findFirstStation(sections);

        Map<Station, Section> stationToSection = new HashMap<>();
        for (Section section : sections) {
            stationToSection.put(section.getUpStation(), section);
        }

        List<Section> sortedSection = new ArrayList<>();

        Section nextSection = findByUpStation(sections, first);
        sortedSection.add(nextSection);
        for (int i = 1; i < sections.size(); i++) {
            Station lastDownStation = nextSection.getDownStation();
            nextSection = stationToSection.get(lastDownStation);
            sortedSection.add(nextSection);
        }

        return sortedSection;
    }

    private Section findByUpStation(List<Section> sections, Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("출발역에 해당되는 구간을 찾을 수 없습니다."));
    }

    private Station findFirstStation(final List<Section> sections) {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());

        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());

        upStations.removeAll(downStations);

        if (upStations.size() > 1) {
            throw new IllegalStateException("상행 종점역이 두 개 이상입니다.");
        }

        return upStations.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행 종점역을 찾을 수 없습니다."));
    }

    private void validateEmpty() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicated(final Section newSection) {
        Station newDownStation = newSection.getDownStation();

        // TODO : Set으로 시간복잡도 줄이는 것 논의하기
        boolean isIncluded = sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newDownStation));

        if (isIncluded) {
            throw new IllegalArgumentException(DUPLICATED_EXCEPTION_MESSAGE);
        }
    }

    private void validateAlreadyExist(final List<Section> sections) {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());

        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        

        if (sections.size() != upStations.size() || sections.size() != downStations.size()) {
            throw new IllegalArgumentException("dddfdfddfdd");
        }
    }

    private void validateSameStation(final Section newSection) {
        if (!this.sections.isEmpty() && !newSection.getUpStation().equals(sections.get(sections.size() - 1).getDownStation())) {
            throw new IllegalArgumentException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
