package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LineRequest {
    
    private final String name;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final String color;

    @JsonCreator
    public LineRequest(
            @JsonProperty("name") final String name,
            @JsonProperty("upStationId") final Long upStationId,
            @JsonProperty("downStationId") final Long downStationId,
            @JsonProperty("distance") final int distance,
            @JsonProperty("color") final String color
    ) {
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.color = color;
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
