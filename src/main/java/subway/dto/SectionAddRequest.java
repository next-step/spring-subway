package subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import subway.domain.Section;

public class SectionAddRequest {

    @NotNull(message = "상행역은 필수 값입니다.")
    private final Long upStationId;

    @NotNull(message = "하행역은 필수 값입니다.")
    private final Long downStationId;

    @NotNull(message = "거리는 필수 값입니다.")
    @Min(value = 1, message = "최소 거리는 1이상 이상입니다.")
    private final Integer distance;

    public SectionAddRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Section toSection(Long lineId) {
        return new Section(null, lineId, upStationId, downStationId, distance);
    }
}
