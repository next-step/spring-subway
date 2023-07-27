package subway.dto;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(final LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public boolean hasNullField() {
        return upStationId == null || downStationId == null || distance == null;
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
}
