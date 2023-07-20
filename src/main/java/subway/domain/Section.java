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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validatePositive(distance);

        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(
                "구간의 상행역과 하행역은 같을 수 없습니다. upStation: " + upStation + ", downStation: "
                    + downStation);
        }
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this(line, upStation, downStation, distance);
        this.id = id;
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 양수여야합니다 distance: \"" + distance + "\"");
        }
    }

    public boolean cannotPrecede(Section other) {
        return !downStation.equals(other.upStation);
    }

    public boolean canPrecede(Section other) {
        return downStation.equals(other.upStation);
    }

    public boolean containsStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(
            station);
    }

    public boolean containsDownStationOf(Section section) {
        return this.upStation.equals(section.downStation) || this.downStation.equals(
            section.downStation);
    }

    public boolean hasSameUpStationOrDownStation(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(section.downStation);
    }

    public List<Section> mergeSections(Section section) {

        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 길이가 같거나 작습니다");
        }

        List<Section> sections = new ArrayList<>();
        if (section.upStation.equals(this.upStation) && !section.downStation.equals(this.downStation)) {
            sections.add(section);
            sections.add(new Section(section.line, section.downStation, this.downStation, this.distance - section.distance));
            return sections;
        }

        if (!section.upStation.equals(this.upStation) && section.downStation.equals(this.downStation)) {
            sections.add(new Section(section.line, this.upStation,  section.upStation, this.distance - section.distance));
            sections.add(section);
            return sections;
        }

        throw new IllegalArgumentException("추가할 구간의 상행역 하행역이 모두 같거나 모두 다를 수 없습니다. 기존 구간: " + this + " 추가할 구간: " + section);
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

    public boolean belongToSameLine(Section section) {
        return this.line.equals(section.line);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id)
            && Objects.equals(line, section.line) && Objects.equals(upStation,
            section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "lineId=" + this.line.getId() +
            "upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }

    public boolean hasDownStationSameAs(Station station) {
        return station.equals(this.downStation);
    }

    public boolean belongTo(Line line) {
        return this.line.equals(line);
    }
}
