package subway.domain;

import subway.exception.IncorrectRequestException;

import java.util.Objects;

public class Section {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 다른 역이어야 합니다.";
    private static final String SECTION_NOT_OVERLAP_EXCEPTION_MESSAGE = "구간이 서로 겹치지 않습니다.";
    private static final String SECTION_NOT_CONNECT_EXCEPTION_MESSAGE = "서로 연결되어 있지 않은 구간을 합칠 수 없습니다.";
    private static final String LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE = "삽입하는 새로운 구간의 거리는 기존 구간보다 짧아야 합니다.";

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
            throw new IncorrectRequestException(SECTION_NOT_CONNECT_EXCEPTION_MESSAGE);
        }
    }

    private void validateLongerThan(final Section other) {
        if (this.distance.shorterOrEqualTo(other.distance)) {
            throw new IncorrectRequestException(LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE);
        }
    }

    private void validateOverlapped(Section other) {
        if (!isSameUpStation(other) && !isSameDownStation(other)) {
            throw new IncorrectRequestException(SECTION_NOT_OVERLAP_EXCEPTION_MESSAGE);
        }
    }

    private void validateDifferent(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IncorrectRequestException(SAME_STATION_EXCEPTION_MESSAGE);
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
