package subway.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(final Long id, final Station upStation, final Station downStation, final Distance distance) {
        validate(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, new Distance(distance));
    }

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        this(null, upStation, downStation, distance);
    }

    private void validate(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 같을 수 없습니다");
        }
    }

    public Section union(final Section other) {
        return new Section(upStation, other.downStation, distance.add(other.distance));
    }

    public Section subtract(final Section other) {
        if (upStation.equals(other.upStation)) {
            return new Section(other.downStation, downStation, distance.subtract(other.distance));
        }

        if (downStation.equals(other.downStation)) {
            return new Section(upStation, other.upStation, distance.subtract(other.distance));
        }

        return this;
    }

    public boolean matchOneStation(final Section other) {
        return downStation.equals(other.downStation) || upStation.equals(other.upStation);
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public int getDistance() {
        return distance.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
