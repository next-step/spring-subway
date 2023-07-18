package subway.dto;

import subway.domain.Section;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    private SectionResponse() {
        /* no-op */
    }

    SectionResponse(final Long id, final Long upStationId, final Long downStationId, final Long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
        return id;
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
