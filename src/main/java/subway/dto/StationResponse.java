package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Station;

public class StationResponse {
    private final Long id;
    private final String name;

    @JsonCreator
    public StationResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName().getValue());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
