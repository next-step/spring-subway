package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    @JsonCreator
    public LineResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name,
            @JsonProperty("color") final String color,
            @JsonProperty("stations") final List<StationResponse> stations
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(final Line line, final List<Station> stations) {
        return new LineResponse(
                line.getId(), line.getName(), line.getColor(),
                stations.stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList())
        );
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

    public List<StationResponse> getStations() {
        return this.stations;
    }
}
