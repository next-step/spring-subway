package subway.domain;

import java.util.Objects;
import subway.exception.IllegalSectionException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        validateStations(upStationId, downStationId);
        validateDistance(distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validateStations(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalSectionException("상행역과 하행역은 달라야 합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간 길이는 0보다 커야한다.");
        }
    }

    public Section downStationId(final Section newSection) {
        return new Section(id, lineId, upStationId, newSection.upStationId, distance - newSection.distance);
    }

    public Section upStationId(final Section newSection) {
        return new Section(id, lineId, newSection.downStationId, downStationId, distance - newSection.distance);
    }

    public boolean isDistanceLessThanOrEqualTo(final Section other) {
        return this.distance <= other.distance;
    }

    public boolean matchDownStationId(final long other) {
        return downStationId == other;
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
        return Objects.equals(id, section.id) && Objects.equals(lineId,
            section.lineId) && Objects.equals(upStationId, section.upStationId)
            && Objects.equals(downStationId, section.downStationId)
            && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", lineId=" + lineId +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            '}';
    }
}
