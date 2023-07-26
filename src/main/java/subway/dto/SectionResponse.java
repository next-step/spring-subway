package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Distance;
import subway.domain.Section;

public class SectionResponse {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @JsonCreator
    public SectionResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("upStationId") final Long upStationId,
            @JsonProperty("downStationId") final Long downStationId,
            @JsonProperty("distance") final Distance distance
    ) {
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
