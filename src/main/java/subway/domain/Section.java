package subway.domain;

import java.util.Objects;

public class Section {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    private Section() {
    }

    public Section(final Long upStationId, final Long downStationId, final Long distance) {
        validate(upStationId, downStationId, distance);

        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    private void validate(final Long upStationId, final Long downStationId, final Long distance) {
        validateContainsUpStationAndDownStation(upStationId, downStationId);
        validateDistanceNotNull(distance);
        validateDistanceLessThanZero(distance);
    }

    private void validateDistanceLessThanZero(final Long distance) {
        if (distance <= 0L) {
            throw new IllegalArgumentException("구간 길이는 0보다 커야합니다.");
        }
    }

    private void validateDistanceNotNull(final Long distance) {
        if (distance == null) {
            throw new IllegalArgumentException("구간 길이 정보는 입력해야 합니다.");
        }
    }

    private void validateContainsUpStationAndDownStation(final Long upStationId, final Long downStationId) {
        if (upStationId == null || downStationId == null) {
            throw new IllegalArgumentException("상행 역 정보와 하행 역 정보는 모두 입력해야 합니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
