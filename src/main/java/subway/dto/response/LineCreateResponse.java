package subway.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;

public class LineCreateResponse {

    private final Long id;
    private final String name;
    private final String color;

    @JsonCreator
    public LineCreateResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name,
            @JsonProperty("color") final String color
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineCreateResponse of(final Line persistLine) {
        return new LineCreateResponse(persistLine.getId(), persistLine.getName(), persistLine.getColor());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }
}
