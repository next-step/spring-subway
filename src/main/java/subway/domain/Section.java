package subway.domain;

import java.util.Objects;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(final Long id, final Station upStation, final Station downStation,
        final Distance distance) {
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
        if (upStation.equals (downStation)){
            throw new SubwayException(ErrorCode.SAME_UP_AND_DOWN_STATION);
        }
    }

    public Section subtract(final Section other) {
        if (upStation.equals(other.upStation)) {
            return new Section(other.downStation, downStation, distance.subtract(other.distance));
        }

        if (downStation.equals(other.downStation)) {
            return new Section(upStation, other.upStation, distance.subtract(other.distance));
        }

        throw new SubwayException(ErrorCode.SECTION_DOES_NOT_CONTAIN_SECTION);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation) && Objects.equals(distance,
            section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    public Section add(Section newSection) {
        if (downStation.isNotEqual(newSection.upStation)) {
            throw new SubwayException(ErrorCode.SECTION_DOES_NOT_CONTAIN_SECTION);
        }
        return new Section(this.upStation, newSection.downStation,
            distance.add(newSection.distance));
    }

    public boolean matchOneStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }
}
