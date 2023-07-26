package subway.domain;

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

    public boolean matchEitherStation(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(
            section.downStation);
    }

    public Section cutBy(Section other) {

        validateLongerDistanceThan(other);

        if (matchUpperPart(other)) {
            return cutUpperPartBy(other);
        }

        if (matchLowerPart(other)) {
            return cutLowerPartBy(other);
        }

        throw new IllegalArgumentException(
            "잘라낼 구간의 상행역 하행역이 모두 같거나 모두 다를 수 없습니다. 기존 구간: " + this + " 잘라낼 구간: " + other);
    }

    private void validateLongerDistanceThan(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 길이가 같거나 작습니다");
        }
    }

    private boolean matchUpperPart(Section section) {
        return section.upStation.equals(this.upStation) && !section.downStation.equals(
            this.downStation);
    }

    private Section cutUpperPartBy(Section section) {
        return new Section(this.id, section.line, section.downStation, this.downStation,
            this.distance - section.distance);
    }

    private boolean matchLowerPart(Section section) {
        return !section.upStation.equals(this.upStation) && section.downStation.equals(
            this.downStation);
    }

    private Section cutLowerPartBy(Section section) {
        return new Section(this.id, section.line, this.upStation, section.upStation,
            this.distance - section.distance);
    }

    public Section extendBy(final Section other) {
        validateConnection(other);
        return new Section(this.id, this.line, this.upStation, other.downStation,
            this.distance + other.distance);
    }

    private void validateConnection(final Section section) {
        if (!this.downStation.equals(section.upStation)) {
            throw new IllegalArgumentException(
                "구간의 하행역이 연장 구간의 상행역과 같지 않으면 연장할 수 없습니다. 기존 구간: " + this + ", 대상 구간: "
                    + section);
        }
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isLongerThan(int distance) {
        return this.distance > distance;
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
