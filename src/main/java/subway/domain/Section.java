package subway.domain;

import subway.exception.section.SectionMinDistanceException;
import subway.exception.section.SectionSameStationException;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validate(upStation, downStation, distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Station upStation, Station downStation, int distance) {
        validateMinDistance(distance);
        validateSameStation(upStation, downStation);
    }

    private void validateMinDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new SectionMinDistanceException();
        }
    }

    private void validateSameStation(Station upStation, Station downStation) {
        if (upStation.isSameId(downStation.getId())) {
            throw new SectionSameStationException();
        }
    }

    public boolean isNotSameDownStation(Long stationId) {
        return !this.downStation.isSameId(stationId);
    }

    public boolean isSameUpStation(Long stationId) {
        return this.upStation.isSameId(stationId);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public void injectionId(long savedId) {
        this.id = savedId;
    }
}
