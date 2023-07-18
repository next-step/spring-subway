package subway.dto.request;

import java.util.Objects;

public final class SectionRegistRequest {

    private Long downStationId;
    private Long upStationId;
    private int distance;

    public SectionRegistRequest() {
    }

    public SectionRegistRequest(final Long downStationId, final Long upStationId, final int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
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
        SectionRegistRequest that = (SectionRegistRequest) o;
        return distance == that.distance && Objects.equals(downStationId, that.downStationId)
            && Objects.equals(upStationId, that.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downStationId, upStationId, distance);
    }
}
