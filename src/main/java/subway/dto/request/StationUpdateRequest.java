package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StationUpdateRequest {
    
    private final String name;

    @JsonCreator
    public StationUpdateRequest(
            final String name
    ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
