package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StationRequest {
    private final String name;

    @JsonCreator
    public StationRequest(
            @JsonProperty("name") final String name
    ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
