package subway.domain;

import java.util.Collections;
import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations(final List<StationPair> stationPairs) {
        this.stations = null;
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
