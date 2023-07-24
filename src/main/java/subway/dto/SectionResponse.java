package subway.dto;

import subway.domain.Section;

public class SectionResponse {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

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
        return this.id;
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
