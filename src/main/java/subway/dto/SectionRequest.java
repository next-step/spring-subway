package subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull(message = "하행 역을 입력해주세요.")
    private Long downStationId;
    @NotNull(message = "상행 역을 입력해주세요.")
    private Long upStationId;
    @NotNull(message = "거리를 입력해주세요.")
    @Min(value = 1, message = "거리는 1 이상의 숫자만 입력 가능합니다.")
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(Long downStationId, Long upStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }
}
