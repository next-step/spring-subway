package subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역은 기존 하행 종점역과 같아야 합니다.";
    private static final String EMPTY_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";

    private final List<Section> sections;
    private final Set<Station> upStations;
    private final Set<Station> downStations;

    public Sections(final List<Section> sections) {
        this.upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
        this.downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        this.sections = sorted(sections);
    }

    public void delete(Station lastStation) {
        if (this.sections.size() <= 1) {
            throw new IllegalStateException("구간은 0개가 될 수 없습니다.");
        }
        int lastIndex = this.sections.size() - 1;
        if (!lastSection().getDownStation().equals(lastStation)) {
            throw new IllegalArgumentException("마지막 구간만 삭제할 수 있습니다.");
        }
        this.sections.remove(lastIndex);
    }

    private List<Section> sorted(final List<Section> sections) {
        if (sections.isEmpty()) {
            return sections;
        }

        Station first = findFirstStation();

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

    private Station findFirstStation() {
        Set<Station> endUpStations = new HashSet<>(this.upStations);
        endUpStations.removeAll(downStations);

        if (endUpStations.size() > 1) {
            throw new IllegalStateException("상행 종점역이 두 개 이상입니다.");
        }

        return endUpStations.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행 종점역을 찾을 수 없습니다."));
    }

    private Section lastSection() {
        validateEmpty();
        return this.sections.get(this.sections.size() - 1);
    }

    public boolean isInsertedMiddle(final Section newSection) {
        boolean containsUpStation = this.upStations.contains(newSection.getUpStation());
        boolean containsDownStation = this.downStations.contains(newSection.getDownStation());

        return containsUpStation || containsDownStation;
    }

    public Section cut(final Section oldSection, final Section newSection) {
        validateDistance(oldSection, newSection);
        int reducedDistance = oldSection.getDistance() - newSection.getDistance();

        if (oldSection.isSameUpStation(newSection)) {
            return new Section(newSection.getDownStation(), oldSection.getDownStation(), reducedDistance);
        }

        return new Section(oldSection.getUpStation(), newSection.getUpStation(), reducedDistance);

    }

    public Section oldSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection) || section.isSameDownStation(newSection))
                .findFirst()
                .orElseThrow();
    }

    public void validateInsert(final Section newSection) {
        Set<Station> allStations = new HashSet<>(upStations);
        allStations.addAll(downStations);

        // TODO: 라인 생성 시 구간 삽입하는 기능 만들면 삭제하기
        if (allStations.isEmpty()) {
            return;
        }

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);
    }

    private void validateDistance(final Section oldSection, final Section newSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.");
        }
    }

    private void validateEmpty() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private void validateExistOnlyOne(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation == hasDownStation) {
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
