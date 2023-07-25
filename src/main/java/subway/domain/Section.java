package subway.domain;

import subway.exception.SubwayException;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final Long distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Section section) {
        this(id, section.lineId, section.upStationId, section.downStationId, section.distance);
    }

    public Section(
            final Long id,
            final Long lineId,
            final Long upStationId,
            final Long downStationId,
            final Long distance
    ) {
        validateSectionValues(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section subtract(final Section from) {
        if (Objects.equals(this.upStationId, from.upStationId)) {
            return new Section(this.lineId, from.downStationId, this.downStationId, this.distance - from.distance);
        }
        return new Section(this.lineId, this.upStationId, from.upStationId, this.distance - from.distance);
    }

    public Long subtractDistance(final Section target) {
        return this.distance - target.distance;
    }

    public boolean isSameUpStationId(final Section target) {
        return Objects.equals(this.upStationId, target.upStationId);
    }

    public boolean isSameDownStationId(final Section target) {
        return Objects.equals(this.downStationId, target.downStationId);
    }

    public boolean doesNotContainsDownStation(final Long targetStationId) {
        return !Objects.equals(this.downStationId, targetStationId);
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

    private void validateSectionValues(final Long upStationId, final Long downStationId, final Long distance) {
        validateStationValues(upStationId, downStationId);
        validateDistanceValue(distance);
    }

    private void validateDistanceValue(final Long distance) {
        if (distance == null) {
            throw new SubwayException("구간 길이 정보는 입력해야 합니다.");
        }
        if (distance <= 0L) {
            throw new SubwayException("구간 길이는 0보다 커야합니다.");
        }
    }

    private void validateStationValues(final Long upStationId, final Long downStationId) {
        if (upStationId == null || downStationId == null) {
            throw new SubwayException("상행 역 정보와 하행 역 정보는 모두 입력해야 합니다.");
        }
    }

    public boolean isHead(final Section target) {
        return Objects.equals(this.downStationId, target.upStationId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
