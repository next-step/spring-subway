package subway.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateSectionRequest {

    @NotNull(message = "상행역을 선택해주세요.")
    private Long upStationId;

    @NotNull(message = "하행역을 선택해주세요.")
    private Long downStationId;

    @NotNull(message = "거리를 입력해주세요.")
    @Positive(message = "거리는 양수여야합니다.")
    private Integer distance;

    public CreateSectionRequest() {
    }

    public CreateSectionRequest(Long upStationId, Long downStationId, Integer distance) {
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
