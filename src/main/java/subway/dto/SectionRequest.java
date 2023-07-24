package subway.dto;

import subway.domain.Section;

public class SectionRequest {

    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    public SectionRequest(final Long upStationId, final Long downStationId, final Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toSection(final Long lineId) {

        return new Section(lineId, this.upStationId, this.downStationId, this.distance);
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public Long getDistance() {
        return this.distance;
    }
}
