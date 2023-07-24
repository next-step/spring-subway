package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Section {

    private Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validatePositive(distance);
        validateUpAndDownStationNotEqual(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 양수여야합니다 distance: \"" + distance + "\"");
        }
    }

    private void validateUpAndDownStationNotEqual(final Station upStation,
        final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(
                "구간의 상행역과 하행역은 같을 수 없습니다. upStation: " + upStation + ", downStation: "
                    + downStation);
        }
    }

    public boolean isUpperSectionOf(Section other) {
        return downStation.equals(other.upStation);
    }

    public boolean containsStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(
            station);
    }

    public boolean hasSameUpStationOrDownStation(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(
            section.downStation);
    }

    public List<Section> mergeSections(Section section) {

        validateLongerDistanceThan(section);

        if (isOnlyUpStationMatch(section)) {
            return mergeUp(section);
        }

        if (isOnlyDownStationMatch(section)) {
            return mergeDown(section);
        }

        throw new IllegalArgumentException(
            "추가할 구간의 상행역 하행역이 모두 같거나 모두 다를 수 없습니다. 기존 구간: " + this + " 추가할 구간: " + section);
    }

    private boolean isOnlyUpStationMatch(Section section) {
        return section.upStation.equals(this.upStation) && !section.downStation.equals(
            this.downStation);
    }

    private List<Section> mergeUp(Section section) {
        List<Section> mergedSections = new ArrayList<>();
        mergedSections.add(section);
        mergedSections.add(new Section(this.id, section.line, section.downStation, this.downStation,
            this.distance - section.distance));
        return mergedSections;
    }

    private boolean isOnlyDownStationMatch(Section section) {
        return !section.upStation.equals(this.upStation) && section.downStation.equals(
            this.downStation);
    }

    private List<Section> mergeDown(Section section) {
        List<Section> mergedSections = new ArrayList<>();
        mergedSections.add(new Section(this.id, section.line, this.upStation, section.upStation,
            this.distance - section.distance));
        mergedSections.add(section);
        return mergedSections;
    }

    private void validateLongerDistanceThan(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 길이가 같거나 작습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean hasUpStationSameAs(Station station) {
        return station.equals(this.upStation);
    }

    public boolean hasDownStationSameAs(Station station) {
        return station.equals(this.downStation);
    }

    public boolean belongTo(Line line) {
        return this.line.equals(line);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line=" + line +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
