package subway.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final Station upStation, final Station downStation, final int distance) {
        validate(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance);
    }

    private void validate(final Station upStation, final Station downStation) {
        if (upStation == downStation) {
            throw new IllegalArgumentException("상행역과 하행역이 같을 수 없습니다");
        }
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
        return distance;
    }

    public Section subtract(final Section other) {
        if (distance <= other.distance) {
            throw new IllegalArgumentException("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다.");
        }

        if (upStation.equals(other.upStation)) {
            return new Section(other.downStation, downStation, distance - other.distance);
        }

        if (downStation.equals(other.downStation)) {
            return new Section(upStation, other.upStation, distance - other.distance);
        }

        throw new IllegalArgumentException("일치하는 역이 없습니다.");
    }

    public boolean isOneStationMatch(final Section other) {
        return downStation.equals(other.downStation) || upStation.equals(other.upStation);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
