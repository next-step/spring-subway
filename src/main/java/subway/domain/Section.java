package subway.domain;

import java.util.Objects;
import subway.exception.ErrorCode;
import subway.exception.SectionException;

public class Section {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 다른 역이어야 합니다.";
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

    private void validateDifferent(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new SectionException(ErrorCode.SAME_SECTION, SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    public Section cutBy(final Section newSection) {
        validateDistance(newSection);

        Distance reducedDistance = distanceDifference(newSection);

        if (isSameUpStation(newSection)) {
            return new Section(newSection.getDownStation(), getDownStation(), reducedDistance);
        }

        return new Section(getUpStation(), newSection.getUpStation(), reducedDistance);
    }

    private void validateDistance(final Section newSection) {
        if (shorterOrEqualTo(newSection)) {
            throw new SectionException(ErrorCode.TOO_LONG_DISTANCE, LONGER_THAN_OLDER_SECTION_EXCEPTION_MESSAGE);
        }
    }

    public boolean isSameUpStation(final Station station) {
        return upStation.equals(station);
    }

    public boolean isSameUpStation(final Section other) {
        return isSameUpStation(other.upStation);
    }

    public boolean isSameDownStation(final Station station) {
        return downStation.equals(station);
    }

    public boolean isSameDownStation(final Section other) {
        return isSameDownStation(other.downStation);
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
