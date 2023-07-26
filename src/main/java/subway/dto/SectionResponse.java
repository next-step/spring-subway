package subway.dto;

import subway.domain.Distance;
import subway.domain.Section;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionResponse() {
        /* no-op */
    }

    SectionResponse(final Long id, final Long upStationId, final Long downStationId, final Distance distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance.getValue();
    }

    public static SectionResponse of(final Section section) {
        return new SectionResponse(
                section.getId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    public Long getId() {
        return this.id;
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
