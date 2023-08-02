package subway.domain;

import java.util.List;

public interface PathManager {

    List<Station> findStationsOfShortestPath(final Station source, final Station target);

    double findDistanceOfShortestPath(final Station source, final Station target);
}
