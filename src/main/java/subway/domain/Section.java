package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.util.Objects;

public class Section {

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

    public Section divideWith(final Section other) {
        validateOverlapped(other);
        validateLongerThan(other);
        Distance reducedDistance = this.distance.subtract(other.distance);

        if (isSameUpStation(other)) {
            return new Section(this.id, other.downStation, this.downStation, reducedDistance);
        }

        return new Section(this.id, this.upStation, other.upStation, reducedDistance);
    }

    public Section connectWith(Section other) {
        validateConnected(other);
        Distance addedDistance = this.distance.add(other.distance);

        if (isUpConnected(other)) {
            return new Section(other.upStation, this.downStation, addedDistance);
        }
        return new Section(this.upStation, other.downStation, addedDistance);
    }

    private void validateConnected(Section other) {
        if (!isUpConnected(other) && !isDownConnected(other)) {
            throw new IncorrectRequestException(
                    ErrorCode.NOT_CONNECTED_SECTION,
                    String.format("구간1 상행역: %s 하행역: %s, 구간2 상행역: %s 하행역: %s", this.upStation.getName(), this.downStation.getName(), other.upStation.getName(), other.downStation.getName())
            );
        }
    }

    private void validateLongerThan(final Section other) {
        if (this.distance.shorterOrEqualTo(other.distance)) {
            throw new IncorrectRequestException(
                    ErrorCode.LONGER_THAN_ORIGIN_SECTION,
                    String.format("기존 구간 길이: %d, 신설 구간 길이: %d", this.distance.getValue(), other.distance.getValue())
            );
        }
    }

    private void validateOverlapped(Section other) {
        if (!isSameUpStation(other) && !isSameDownStation(other)) {
            throw new InternalStateException(
                    ErrorCode.NOT_OVERLAPPED_SECTION,
                    String.format("기존 구간 상행역: %s 하행역: %s, 신설 구간 상행역: %s 하행역: %s", this.upStation.getName(), this.downStation.getName(), other.upStation.getName(), other.downStation.getName())
            );
        }
    }

    private void validateDifferent(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IncorrectRequestException(
                    ErrorCode.SAME_STATION_SECTION,
                    String.format("상행역: %s, 하행역 %s", upStation.getName(), downStation.getName())
            );
        }
    }

    public boolean isSameUpStation(final Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean isSameDownStation(final Section other) {
        return downStation.equals(other.downStation);
    }

    public boolean isUpConnected(final Section other) {
        return upStation.equals(other.downStation);
    }

    public boolean isDownConnected(final Section other) {
        return downStation.equals(other.upStation);
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
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
