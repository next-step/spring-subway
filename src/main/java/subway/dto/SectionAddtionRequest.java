package subway.dto;

public class SectionAddtionRequest {
    private final Long downStationsId;
    private final Long upStationsId;
    private final int distance;

    public SectionAddtionRequest(Long downStationsId, Long upStationsId, int distance) {
        this.downStationsId = downStationsId;
        this.upStationsId = upStationsId;
        this.distance = distance;
    }

    public Long getDownStationsId() {
        return downStationsId;
    }

    public Long getUpStationsId() {
        return upStationsId;
    }

    public int getDistance() {
        return distance;
    }
}
