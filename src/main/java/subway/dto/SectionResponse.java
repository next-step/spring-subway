package subway.dto;

import subway.domain.Section;

public class SectionResponse {

    private final Long id;
    private final Long lineId;
    private final Long downStationId;
    private final Long upStationId;
    private final Double distance;

    public SectionResponse(final Long id, final Long lineId, final Long downStationId, final Long upStationId, final Double distance) {
        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionResponse of(final Section section) {
        return new SectionResponse(
                section.getId(),
                section.getLineId(),
                section.getDownStationId(),
                section.getUpStationId(),
                section.getDistance()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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
