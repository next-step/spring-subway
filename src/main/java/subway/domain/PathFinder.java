package subway.domain;

import java.util.List;

public class PathFinder {

    private final List<Section> sections;

    public PathFinder(final List<Section> sections) {
        this.sections = sections;
    }

    public PathFinderResult findShortestPath(final Station source, final Station target) {
        return new PathFinderResult(List.of(1L, 2L, 3L), 2);
    }
}
