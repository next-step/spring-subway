package subway.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StationCreateRequest {
    
    private final String name;

    @JsonCreator
    public StationCreateRequest(
            final String name
    ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
