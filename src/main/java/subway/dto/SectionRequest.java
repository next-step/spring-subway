package subway.dto;

import subway.domain.Distance;
import subway.domain.Section;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
        /* no-op */
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toSection(final Long lineId) {
        return new Section(lineId, this.upStationId, this.downStationId, new Distance(this.distance));
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }
}
