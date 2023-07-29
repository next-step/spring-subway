package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Distance;
import subway.domain.Section;

public class SectionCreateRequest {

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @JsonCreator
    public SectionCreateRequest(
            @JsonProperty("upStationId") final Long upStationId,
            @JsonProperty("downStationId") final Long downStationId,
            @JsonProperty("distance") final int distance
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
