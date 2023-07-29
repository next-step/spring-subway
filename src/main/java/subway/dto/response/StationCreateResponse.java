package subway.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Station;

public class StationCreateResponse {

    private final Long id;
    private final String name;

    @JsonCreator
    public StationCreateResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static StationCreateResponse of(Station station) {
        return new StationCreateResponse(station.getId(), station.getName().getValue());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
