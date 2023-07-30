package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import subway.exception.ErrorCode;
import subway.exception.FindPathException;

public class PathFinderResult {

    private final List<Long> paths;
    private final Distance distance;

    public PathFinderResult(final GraphPath<Long, DefaultWeightedEdge> paths) {
        validateExistsPath(paths);

        this.paths = paths.getVertexList();
        this.distance = new Distance((int) paths.getWeight());
    }

    private void validateExistsPath(final GraphPath<Long, DefaultWeightedEdge> paths) {
        if (paths == null) {
            throw new FindPathException(ErrorCode.NOT_FOUND_PATH, "경로가 존재하지 않습니다.");
        }
    }

    public List<Long> getPaths() {
        return paths;
    }

    public Distance getDistance() {
        return distance;
    }
}
