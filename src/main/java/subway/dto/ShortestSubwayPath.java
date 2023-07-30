package subway.dto;

import java.util.List;
import subway.domain.Station;
import subway.dto.response.StationResponse;

public class ShortestSubwayPath {

    private List<StationResponse> stations;
    private double distance;

    public ShortestSubwayPath() {
    }

    public ShortestSubwayPath(List<Station> stations, double distance) {
        this.stations = StationResponse.listOf(stations);
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
