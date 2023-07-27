package subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.List;

public class Path {

    private final List<Station> path;
    private final Distance distance;

    public Path(List<Sections> allSections, Station source, Station target) {
        validateDifferentSourceTarget(source, target);
        StationGraph stationGraph = new StationGraph(allSections);

        this.path = stationGraph.getPath(source, target);
        this.distance = stationGraph.getDistance(source, target);
    }

    private void validateDifferentSourceTarget(Station source, Station target) {
        if (source.equals(target)) {
            throw new IncorrectRequestException(ErrorCode.SAME_SOURCE_TARGET, String.format("출발역: %s, 도착역: %s", source.getName(), target.getName()));
        }
    }

    public List<Station> getPath() {
        return path;
    }

    public Distance getDistance() {
        return distance;
    }
}
