package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LineCreateRequest {

    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @JsonCreator
    public LineCreateRequest(
            @JsonProperty("name") final String name,
            @JsonProperty("color") final String color,
            @JsonProperty("upStationId") final Long upStationId,
            @JsonProperty("downStationId") final Long downStationId,
            @JsonProperty("distance") final int distance
    ) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return this.name;
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

    public String getColor() {
        return this.color;
    }
}
