package subway.dto;

import java.util.Objects;

public class SectionRequest {

    private final String upStationId;
    private final String downStationId;
    private final Integer distance;

    public SectionRequest(String upStationId, String downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
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
        return Objects.equals(upStationId, that.upStationId) && Objects.equals(downStationId,
                that.downStationId) && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionRequest{" +
                "upStationId='" + upStationId + '\'' +
                ", downStationId='" + downStationId + '\'' +
                ", distance=" + distance +
                '}';
    }
}
