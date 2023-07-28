package subway.dto;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import subway.domain.Station;

public class PathResponse {

    private final List<Station> stations;
    private final Long distance;

    public PathResponse(final List<Station> stations, final Long distance) {
        this.stations = stations;
        this.distance = distance;
    }


    public static PathResponse of(GraphPath<Station, DefaultWeightedEdge> route) {
        return new PathResponse(route.getVertexList(), (long) route.getWeight());
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
