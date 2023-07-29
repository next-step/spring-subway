package subway.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import subway.domain.Station;

public class PathResponse {

    private final List<StationResponse> stations;
    private final long distance;

    PathResponse(final List<StationResponse> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(final List<Long> shortestPathStationIds, final Map<Long, Station> stations, final long distance) {
        final List<StationResponse> stationResponses = shortestPathStationIds
                .stream()
                .map(id -> StationResponse.of(stations.get(id)))
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, distance);
    }

    public List<StationResponse> getStations() {
        return this.stations;
    }

    public Long getDistance() {
        return this.distance;
    }
}
