package subway.domain;

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

    public boolean containsDownStationOf(Section section) {
        return this.upStation.equals(section.downStation) || this.downStation.equals(section.downStation);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
