package subway.domain.fixture;

import subway.domain.Station;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StationFixture {
    public static Station createDefaultStation() {
        return new Station("낙성대");
    }

    public static Station createStation(String stationName) {
        return new Station(stationName);
    }

    public static List<Station> createStations(String... stationName) {
        return Arrays.stream(stationName)
                .map(Station::new)
                .collect(toList());
    }
}
