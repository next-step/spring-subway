package subway.dto.request;

public final class SectionRegisterRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRegisterRequest() {
    }

    public SectionRegisterRequest(
        Long upStationId,
        Long downStationId,
        int distance
    ) {
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

    public int getDistance() {
        return distance;
    }

}
