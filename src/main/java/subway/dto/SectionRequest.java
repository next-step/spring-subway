package subway.dto;

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
    public String toString() {
        return "SectionRequest{" +
                "upStationId='" + upStationId + '\'' +
                ", downStationId='" + downStationId + '\'' +
                ", distance=" + distance +
                '}';
    }
}
