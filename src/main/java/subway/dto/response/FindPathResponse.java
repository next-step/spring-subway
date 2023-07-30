package subway.dto.response;

import java.util.List;

public class FindPathResponse {

    private final List<FindStationResponse> stations;
    private final Integer distance;

    public FindPathResponse(List<FindStationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<FindStationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
