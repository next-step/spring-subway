package subway.domain;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;
    private Long nextSectionId;
    private Long prevSectionId;

    private Section() {
        /* no-op */
    }

    public Section(
            final Long id,
            final Long lineId,
            final Long upStationId,
            final Long downStationId,
            final Long distance,
            final Long nextSectionId,
            final Long prevSectionId
    ) {
        validate(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.nextSectionId = nextSectionId;
        this.prevSectionId = prevSectionId;
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

    public Long getNextSectionId() {
        return nextSectionId;
    }

    public Long getPrevSectionId() {
        return prevSectionId;
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
        return Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance) && Objects.equals(nextSectionId, section.nextSectionId) && Objects.equals(prevSectionId, section.prevSectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance, nextSectionId, prevSectionId);
    }

    public static class Builder {

        private Long id;
        private Long lineId;
        private Long upStationId;
        private Long downStationId;
        private Long distance;
        private Long nextSectionId;
        private Long prevSectionId;

        private Builder() {
            /* no-op */
        }

        public Builder(final Long lineId, final Long upStationId, final Long downStationId, final Long distance) {
            this.lineId = lineId;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public Builder nextSectionId(final Long nextSectionId) {
            this.nextSectionId = nextSectionId;
            return this;
        }

        public Builder prevSectionId(final Long prevSectionId) {
            this.prevSectionId = prevSectionId;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }

    private Section(final Builder builder) {
        this(
                builder.id,
                builder.lineId,
                builder.upStationId,
                builder.downStationId,
                builder.distance,
                builder.nextSectionId,
                builder.prevSectionId
        );
    }
}
