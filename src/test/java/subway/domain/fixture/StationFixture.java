package subway.domain.fixture;

import subway.domain.Station;

public class StationFixture {

    public static Station createStationA() {
        return new Station(1L, "A");
    }

    public static Station createStationB() {
        return new Station(2L, "B");
    }

    public static Station createStationC() {
        return new Station(3L, "C");
    }

    public static Station createStationD() {
        return new Station(4L, "D");
    }

    public static Station createStation(String name){
        return new Station(0L, name);
    }
}
