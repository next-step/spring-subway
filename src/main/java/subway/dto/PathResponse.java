package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Distance;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Integer distance;

    @JsonCreator
    public PathResponse(
            @JsonProperty("stations") final List<StationResponse> stations,
            @JsonProperty("distance") final Integer distance
    ) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(final List<Station> stations, final Distance distance) {
        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, distance.getValue());
    }

    public List<StationResponse> getStations() {
        return this.stations;
    }

    public Integer getDistance() {
        return this.distance;
    }
}
