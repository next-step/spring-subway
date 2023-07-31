package subway.dto.response;

import subway.domain.Distance;
import subway.domain.Section;

public class SectionCreateResponse {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionCreateResponse(
            final Long id,
            final Long upStationId,
            final Long downStationId,
            final Distance distance
    ) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance.getValue();
    }

    public static SectionCreateResponse of(final Section section) {
        return new SectionCreateResponse(
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
