package subway.dto;

import java.util.Objects;

public class SectionRequest {

    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectionRequest)) {
            return false;
        }
        SectionRequest that = (SectionRequest) o;
        return Objects.equals(distance, that.distance) && Objects.equals(downStationId, that.downStationId)
                && Objects.equals(upStationId, that.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
