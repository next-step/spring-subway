package subway.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineFindResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationFindResponse> stations;

    @JsonCreator
    public LineFindResponse(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name,
            @JsonProperty("color") final String color,
            @JsonProperty("stations") final List<StationFindResponse> stations
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineFindResponse of(final Line line, final List<Station> stations) {
        return new LineFindResponse(
                line.getId(), line.getName(), line.getColor(),
                stations.stream()
                        .map(StationFindResponse::of)
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

    public List<StationFindResponse> getStations() {
        return this.stations;
    }
}
