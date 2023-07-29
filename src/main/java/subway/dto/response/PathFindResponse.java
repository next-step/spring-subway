package subway.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Distance;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathFindResponse {

    private final List<StationFindResponse> stations;
    private final Integer distance;

    @JsonCreator
    public PathFindResponse(
            @JsonProperty("stations") final List<StationFindResponse> stations,
            @JsonProperty("distance") final Integer distance
    ) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathFindResponse of(final List<Station> stations, final Distance distance) {
        final List<StationFindResponse> stationFindRespons = stations.stream()
                .map(StationFindResponse::of)
                .collect(Collectors.toList());

        return new PathFindResponse(stationFindRespons, distance.getValue());
    }

    public List<StationFindResponse> getStations() {
        return this.stations;
    }

    public Integer getDistance() {
        return this.distance;
    }
}
