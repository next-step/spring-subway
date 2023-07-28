package subway.domain;

import java.util.Objects;
import subway.exception.IllegalSectionException;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;

    public Section(final Long id, final Line line, final Station upStation,
        final Station downStation, final Integer distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);

        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Line line, final Station upStation, final Station downStation,
        final Integer distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section narrowToUpDirection(final Station narrowdDownStation, final int narrowAmount) {
        int narrowedDistance = distance - narrowAmount;
        return new Section(id, line, upStation, narrowdDownStation, narrowedDistance);
    }

    public Section narrowToDownDirection(final Station narrowedUpStation, final int narrowAmount) {
        int narrowedDistance = distance - narrowAmount;
        return new Section(id, line, narrowedUpStation, downStation, narrowedDistance);
    }

    public Section extendToUpDirection(final Section upDirection) {
        int extendedDistance = upDirection.distance + distance;
        return new Section(id, line, upDirection.getUpStation(), downStation, extendedDistance);
    }

    public boolean isDistanceLessThanOrEqualTo(final int other) {
        return this.distance <= other;
    }

    private void validateStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalSectionException("상행역과 하행역은 달라야 합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간 길이는 0보다 커야한다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
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
        return Objects.equals(id, section.id) && Objects.equals(line,
            section.line) && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation)
            && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", lineId=" + line +
            ", upStationId=" + upStation +
            ", downStationId=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
