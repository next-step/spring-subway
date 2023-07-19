package subway.dto;

import subway.domain.Section;

public class SectionRequest {

    private Long downStationId;

    private Long upStationId;

    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(final String downStationId, final String upStationId, final Integer distance) {
        this.downStationId = Long.parseLong(downStationId);
        this.upStationId = Long.parseLong(upStationId);
        this.distance = distance;
    }

    public Section to(Long lineId) {
        return new Section(lineId, downStationId, upStationId, distance);
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
