package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.PathException;
import subway.exception.StationException;

import java.util.List;

public class PathFinder {

    private final Integer distance;
    private final List<Station> path;

    public PathFinder(List<Section> sections, Station startStation, Station endStation) {
        validateStartStation(startStation);
        validateEndStation(endStation);
        validateSameStations(startStation, endStation);

        DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = getShortestPath(sections, startStation, endStation);
        GraphPath graphPath = shortestPath.getPath(startStation, endStation);
        validateUnlinked(graphPath);

        this.path = graphPath.getVertexList();
        this.distance = (int) shortestPath.getPathWeight(startStation, endStation);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> getShortestPath(List<Section> sections, Station startStation, Station endStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(startStation);
        graph.addVertex(endStation);

        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            graph.addVertex(upStation);
            graph.addVertex(downStation);

            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
        return new DijkstraShortestPath<>(graph);
    }

    private static void validateUnlinked(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        if (graphPath == null) {
            throw new PathException("startStation와 endStation이 연결되어있지 않습니다");
        }
    }

    private void validateSameStations(Station startStation, Station endStation) {
        if (startStation.equals(endStation)) {
            throw new PathException("startStation과 endStation이 동일합니다");
        }
    }

    private void validateEndStation(Station endStation) {
        if (endStation == null) {
            throw new StationException("endStation이 존재하지 않습니다");
        }
    }

    private void validateStartStation(Station startStation) {
        if (startStation == null) {
            throw new StationException("startStation이 존재하지 않습니다");
        }
    }

    public Integer getDistance() {
        return distance;
    }

    public List<Station> getPath() {
        return path;
    }
}
