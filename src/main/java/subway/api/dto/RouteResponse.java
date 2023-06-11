package subway.api.dto;

import subway.domain.entity.Station;
import subway.domain.vo.Route;

import java.util.List;

public class RouteResponse {
    private List<Station> stations;
    private int distance;
    private int price;

    public RouteResponse(List<Station> stations, int distance, int price) {
        this.stations = stations;
        this.distance = distance;
        this.price = price;
    }

    public static RouteResponse of(Route route, int price) {
        return new RouteResponse(route.getStations(), route.getDistance(), price);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getPrice() {
        return price;
    }
}
