package subway.dto;

import subway.domain.Section;

public class SectionRequest {


    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(final String upStationId, final String downStationId, final Integer distance) {
        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = distance;
    }

    public Section to(Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
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
