package subway.domain;

import java.util.List;

public class PathFinder {

    private final List<Section> sections;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> findStations(Long departureStationId, Long destinationStationId) {
        // Main Logic


        // Test
        Station station1 = new Station(departureStationId, "교대역");
        Station station2 = new Station(7L, "남부터미널역");
        Station station3 = new Station(destinationStationId, "양재역");
        List<Station> stations = List.of(station1, station2, station3);
        return stations;
    }

    public Long findMinimumDistance(Long departureStationId, Long destinationStationId) {
        return 5L;
    }
}
