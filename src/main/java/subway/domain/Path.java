package subway.domain;

import java.util.List;

public class Path {

    private List<Station> stations;
    private int distance;
    private int charge;

    public Path(List<Station> stations, int distance, int charge) {
        this.stations = stations;
        this.distance = distance;
        this.charge = charge;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getCharge() {
        return charge;
    }
}
