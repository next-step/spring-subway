package subway.application;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.FindPathResponse;
import subway.exception.PathException;

import java.util.List;

@Service
public class PathFinderService {

    public FindPathResponse findPath(List<Section> sections, Station startStation, Station endStation) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = getShortestPath(sections, startStation, endStation);
        GraphPath graphPath = shortestPath.getPath(startStation, endStation);
        validateUnlinked(graphPath);

        List<Station> stations = graphPath.getVertexList();
        int distance = (int) shortestPath.getPathWeight(startStation, endStation);

        return FindPathResponse.from(stations, distance);
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
}
