package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PathRequest {

    private final Long source;
    private final Long target;

    @JsonCreator
    public PathRequest(
            @JsonProperty("source") final Long source,
            @JsonProperty("target") final Long target
    ) {
        this.source = source;
        this.target = target;
    }

    public Long getSource() {
        return this.source;
    }

    public Long getTarget() {
        return this.target;
    }
}
