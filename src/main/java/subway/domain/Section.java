package subway.domain;

import java.util.Objects;
import java.util.StringJoiner;

public class Section {
    @Override
    public String toString() {
        return new StringJoiner(", ", Section.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("lineId=" + lineId)
                .add("upStationId=" + upStationId)
                .add("downStationId=" + downStationId)
                .add("distance=" + distance)
                .add("preStationId=" + preStationId)
                .add("postStationId=" + postStationId)
                .toString();
    }

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;
    private Long preStationId;
    private Long postStationId;

    private Section() {
    }

    public Section(final Long upStationId, final Long downStationId, final Long distance) {
        this(null, null, upStationId, downStationId, distance, null, null);
    }

    public Section(
            final Long id,
            final Long lineId,
            final Long upStationId,
            final Long downStationId,
            final Long distance,
            final Long preStationId,
            final Long postStationId
    ) {
        validate(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.preStationId = preStationId;
        this.postStationId = postStationId;
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

    public Long getDistance() {
        return distance;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getPostStationId() {
        return postStationId;
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
        return Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance) && Objects.equals(preStationId, section.preStationId) && Objects.equals(postStationId, section.postStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance, preStationId, postStationId);
    }
}
