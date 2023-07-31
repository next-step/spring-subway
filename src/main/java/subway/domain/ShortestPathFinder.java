package subway.domain;

import java.util.List;

public interface ShortestPathFinder {

    void calculatePath(List<Section> sections, Station source, Station target);
    List<Station> getStations();
    int getDistance();
}
