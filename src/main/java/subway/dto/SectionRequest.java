package subway.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull(message = "상행역 id는 필수 항목입니다.")
    private Long upStationId;
    @NotNull(message = "하행역 id는 필수 항목입니다.")
    private Long downStationId;
    @NotNull(message = "거리는 필수 항목입니다.")
    private Integer distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final Integer distance) {
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
}
