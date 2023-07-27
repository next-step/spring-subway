package subway.dto.response;

import java.util.List;

public class PathResponse {

    public List<StationResponse> stations;
    public Double distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getDistance() {
        return distance;
    }

}
