package subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역은 기존 하행 종점역과 같아야 합니다.";
    private static final String DUPLICATED_EXCEPTION_MESSAGE = "새로운 구간의 하행역은 기존 노선에 등록되어 있지 않은 역이어야 합니다.";
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

    public void insert(final Section newSection) {
        boolean existUpStation = existUpStation(newSection);
        if (!existUpStation && newSection.getDownStation().equals(this.sections.get(0).getUpStation())) {
            sections.add(0, newSection);
            return;
        }
        if (!existUpStation) {
            Station downStation = newSection.getDownStation();
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).getDownStation().equals(downStation)) {
                    if (newSection.getDistance() >= sections.get(i).getDistance()) {
                        throw new IllegalArgumentException("삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.");
                    }
                    Station upStation = sections.get(i).getUpStation();
                    int distance = sections.get(i).getDistance();
                    sections.remove(i);
                    Section preceedSection = new Section(upStation, newSection.getUpStation(), distance - newSection.getDistance());
                    sections.add(i, preceedSection);
                    sections.add(i+1, newSection);
                    return;
                }
            }
        }
        if (existUpStation && newSection.getUpStation().equals(this.sections.get(this.sections.size() - 1).getDownStation())) {
            sections.add(newSection);
            return;
        }
        if (existUpStation) {
            Station upStation = newSection.getUpStation();
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).getUpStation().equals(upStation)) {
                    if (newSection.getDistance() >= sections.get(i).getDistance()) {
                        throw new IllegalArgumentException("삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.");
                    }
                    Station downStation = sections.get(i).getDownStation();
                    int distance = sections.get(i).getDistance();
                    sections.remove(i);
                    Section followSection = new Section(newSection.getDownStation(), downStation, distance - newSection.getDistance());
                    sections.add(i, newSection);
                    sections.add(i + 1, followSection);
                    return;
                }
            }
        }
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

    private void validateEmpty() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private boolean existUpStation(final Section newSection) {
        Set<Station> allStations = new HashSet<>(upStations);
        allStations.addAll(downStations);

        boolean hasUpStation = allStations.contains(newSection.getUpStation());
        boolean hasDownStation = allStations.contains(newSection.getDownStation());

        validateExistOnlyOne(hasUpStation, hasDownStation);

        return hasUpStation;
    }

    private void validateExistOnlyOne(boolean hasUpStation, boolean hasDownStation) {
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
