package subway.dto;

import javax.validation.constraints.NotNull;

public class SectionAdditionRequest {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    private SectionAdditionRequest() {
    }

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
