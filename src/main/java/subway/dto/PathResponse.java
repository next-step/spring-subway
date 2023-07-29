package subway.dto;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import subway.domain.Station;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Long distance;

    public PathResponse(final List<StationResponse> stations, final Long distance) {
        this.stations = stations;
        this.distance = distance;
    }


    public static PathResponse of(GraphPath<Station, DefaultWeightedEdge> route) {
        return new PathResponse(StationResponse.of(route.getVertexList()),
            (long) route.getWeight());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
