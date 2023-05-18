package subway.domain;

import subway.exception.SectionNotConnectingStationException;
import subway.exception.SectionMinDistanceException;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validate(Long upStationId, Long downStationId, int distance) {
        validateMinDistance(distance);
        validateConnectingStation(upStationId, downStationId);
    }

    private void validateMinDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new SectionMinDistanceException();
        }
    }

    private void validateConnectingStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SectionNotConnectingStationException();
        }
    }

    public boolean isNotSameDownStation(Long stationId) {
        return !this.downStationId.equals(stationId);
    }

    public boolean isSameUpStation(Long stationId) {
        return this.upStationId.equals(stationId);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void injectionId(long savedId) {
        this.id = savedId;
    }
}
