package subway.dto.response;

import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class FindPathResponse {

    private final List<FindStationResponse> stations;
    private final Integer distance;

    public FindPathResponse(List<FindStationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static FindPathResponse from(List<Station> stations, Integer distance) {
        List<FindStationResponse> findStations = stations.stream()
                .map(station -> FindStationResponse.of(station))
                .collect(Collectors.toList());

        return new FindPathResponse(findStations, distance);
    }

    public List<FindStationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
