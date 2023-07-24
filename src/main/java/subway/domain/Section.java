package subway.domain;

import java.util.Objects;

public class Section {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 다른 역이어야 합니다.";

    private Long id;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    public Section() {
    }

    public Section(final Station upStation, final Station downStation, final Integer distance) {
        this(null, upStation, downStation, new Distance(distance));
    }

    public Section(final Long id, final Station upStation, final Station downStation, final Integer distance) {
        this(id, upStation, downStation, new Distance(distance));
    }

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(final Long id, final Station upStation, final Station downStation, final Distance distance) {
        validateDifferent(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDifferent(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    public boolean isSameUpStation(final Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean isSameDownStation(final Section other) {
        return downStation.equals(other.downStation);
    }

    public boolean shorterOrEqualTo(final Section other) {
        return distance.shorterOrEqualTo(other.distance);
    }

    public Distance distanceDifference(final Section other) {
        return this.distance.subtract(other.distance);
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
