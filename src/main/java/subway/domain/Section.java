package subway.domain;

import subway.exception.IllegalSectionException;

public final class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;

    public Section(final Long id,
                   final Line line,
                   final Station upStation,
                   final Station downStation,
                   final Integer distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);

        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        this(null, line, upStation, downStation, distance);
    }

    private void validateStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalSectionException("상행역과 하행역은 같을 수 없습니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간 길이는 0보다 커야합니다.");
        }
    }

    public Section updateDownStation(final Section newSection) {
        return new Section(id, line, upStation, newSection.upStation, distance - newSection.distance);
    }

    public Section updateUpStation(final Section newSection) {
        return new Section(id, line, newSection.downStation, downStation, distance - newSection.distance);
    }

    public boolean hasStation(final Station station) {
        return station.equals(upStation) || station.equals(downStation);
    }

    public boolean hasStation(final long stationId) {
        return upStation.equalsId(stationId) || downStation.equalsId(stationId);
    }

    public boolean isDistanceGreaterThan(final int other) {
        return this.distance > other;
    }

    public boolean compareUpStationId(final Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean equalsUpStation(final Station upStation) {
        return upStation.equals(this.upStation);
    }

    public boolean equalsDownStation(final Section other) {
        return other.downStation.equals(this.downStation);
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
}
