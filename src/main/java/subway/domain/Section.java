package subway.domain;

import subway.exception.SubwayException;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId,
            final Long distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(
            final Long id,
            final Long lineId,
            final Long upStationId,
            final Long downStationId,
            final Long distance
    ) {
        validate(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean containsStation(final Long stationId) {
        return Objects.equals(this.upStationId, stationId) || Objects.equals(this.downStationId,
                stationId);
    }

    public boolean isSameDownStationId(final Long stationId) {
        return this.downStationId.equals(stationId);
    }

    public Long subtractDistance(final Long distance) {
        return this.distance - distance;
    }

    public boolean containsStations(final Long upStationId, final Long downStationId) {
        return Objects.equals(this.upStationId, upStationId) || Objects.equals(this.downStationId,
                downStationId);
    }

    public Section subtract(final Section requestSection) {
        if (Objects.equals(this.upStationId, requestSection.upStationId)) {
            return new Section(this.lineId, requestSection.upStationId, this.downStationId,
                    this.distance - requestSection.distance);
        }
        return new Section(this.lineId, this.upStationId, requestSection.downStationId,
                this.distance - requestSection.distance);
    }

    public Long getId() {
        return this.id;
    }

    public Long getLineId() {
        return this.lineId;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public Long getDistance() {
        return this.distance;
    }

    private void validate(final Long upStationId, final Long downStationId, final Long distance) {
        validateSameStation(upStationId, downStationId);
        validateContainsUpStationAndDownStation(upStationId, downStationId);
        validateDistanceNotNull(distance);
        validateDistanceLessThanZero(distance);
    }

    private void validateSameStation(final Long upStationsId, final Long downStationsId) {
        if (Objects.equals(upStationsId, downStationsId)) {
            throw new SubwayException("입력된 하행역과 상행역이 같습니다.");
        }
    }

    private void validateDistanceLessThanZero(final Long distance) {
        if (distance <= 0L) {
            throw new SubwayException("구간 길이는 0보다 커야합니다.");
        }
    }

    private void validateDistanceNotNull(final Long distance) {
        if (distance == null) {
            throw new SubwayException("구간 길이 정보는 입력해야 합니다.");
        }
    }

    private void validateContainsUpStationAndDownStation(final Long upStationId,
            final Long downStationId) {
        if (upStationId == null || downStationId == null) {
            throw new SubwayException("상행 역 정보와 하행 역 정보는 모두 입력해야 합니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(id, section.id)
                && Objects.equals(lineId, section.lineId)
                && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
