package subway.dto;

import subway.domain.Section;

public class SectionRequest {

    private Long downStationId;

    private Long upStationId;

    private Double distance;

    private SectionRequest() {
    }

    public SectionRequest(final String downStationId, final String upStationId, final Double distance) {
        this.downStationId = Long.parseLong(downStationId);
        this.upStationId = Long.parseLong(upStationId);
        this.distance = distance;
    }

    public Section toEntity(Long lineId) {
        return new Section(lineId, downStationId, upStationId, distance);
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Double getDistance() {
        return distance;
    }
}
