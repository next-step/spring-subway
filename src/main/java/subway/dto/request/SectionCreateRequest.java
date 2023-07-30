package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.domain.Distance;
import subway.domain.Section;

public class SectionCreateRequest {

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @JsonCreator
    public SectionCreateRequest(
            final Long upStationId,
            final Long downStationId,
            final int distance
    ) {
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
