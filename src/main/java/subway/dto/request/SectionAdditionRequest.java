package subway.dto.request;

public class SectionAdditionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionAdditionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
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
}
