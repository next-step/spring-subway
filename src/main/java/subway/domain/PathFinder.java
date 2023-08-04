package subway.domain;

import java.util.List;
import subway.dto.PathFinderResult;

public interface PathFinder {

    PathFinderResult findShortestPath(List<Section> sections, Station source, Station target);
}
