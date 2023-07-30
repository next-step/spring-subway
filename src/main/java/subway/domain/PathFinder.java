package subway.domain;

import java.util.List;

public class PathFinder {

    private final Long distance;
    private final List<Station> path;

    public PathFinder(List<Section> sections, Station startStation, Station endStation) {
        this.path = null;
        this.distance = null;
    }

    public Long getDistance() {
        return distance;
    }

    public List<Station> getPath() {
        return path;
    }
}
