package subway.domain;

import java.util.Objects;

public class Section {

    public static final int POSSIBLE_DISTANCE = 0;
    private  Long id;
    private Station upStation;
    private Station downStation;
    private Line line;
    private Distance distance;

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this(null, upStation, downStation, line, distance);
    }

    public Section(Long id, Station upStation, Station downStation, Line line, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = new Distance(distance);
    }

    public boolean upStationEquals(Station station) {
        return upStation.equals(station);
    }

    public boolean downStationEquals(Station station) {
        return downStation.equals(station);
    }

    public boolean isOverDistance(Distance distance) {
        return (this.distance.compareDistance(distance) <= POSSIBLE_DISTANCE);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
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
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", line=" + line +
            ", distance=" + distance +
            '}';
    }
}
