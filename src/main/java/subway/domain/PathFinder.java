package subway.domain;

import java.util.List;

public interface PathFinder {

    List<Station> findShortestStations();

    Double findShortestDistance();
}
