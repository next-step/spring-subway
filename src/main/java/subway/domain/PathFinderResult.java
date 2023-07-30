package subway.domain;

import java.util.List;

public class PathFinderResult {

    private final List<Long> paths;
    private final Integer distance;

    public PathFinderResult(List<Long> paths, Integer distance) {
        this.paths = paths;
        this.distance = distance;
    }

    public List<Long> getPaths() {
        return paths;
    }

    public Integer getDistance() {
        return distance;
    }
}
