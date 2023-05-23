package subway.api.dto;

import subway.domain.entity.Section;

import javax.validation.constraints.Min;

public class SectionRequest {
    private Long downStationId;
    private Long upStationId;
    @Min(value = 1, message = "거리는 1 이상의 숫자여야 합니다.")
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long downStationId, Long upStationId, int distance) {
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

    public int getDistance() {
        return distance;
    }

    public Section toDomain(Long lineId) {
        return new Section(downStationId, upStationId, lineId, distance);
    }
}
