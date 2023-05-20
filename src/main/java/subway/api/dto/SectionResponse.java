package subway.api.dto;

import subway.domain.Section;

public class SectionResponse {
    private Long id;
    private Long downStationId;
    private Long upStationId;
    private Long lineId;
    private int distance;

    public SectionResponse(Long id, Long downStationId, Long upStationId, Long lineId, int distance) {
        this.id = id;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getDownStationId(), section.getUpStationId(), section.getLineId(), section.getDistance());
    }
}
