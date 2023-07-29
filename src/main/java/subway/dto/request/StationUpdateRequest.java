package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StationUpdateRequest {
    private final String name;

    @JsonCreator
    public StationUpdateRequest(
            @JsonProperty("name") final String name
    ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
