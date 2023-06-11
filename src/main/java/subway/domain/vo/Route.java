package subway.domain.vo;

import subway.domain.entity.Station;

import java.util.List;

public class Route {
    private final List<Station> stations;
    private final int distance;
    private final int price;

    public Route(List<Station> stations, int distance, int price) {
        this.stations = stations;
        this.distance = distance;
        this.price = price;
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
