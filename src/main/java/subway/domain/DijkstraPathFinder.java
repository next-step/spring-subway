package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DijkstraPathFinder implements PathFinder{

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public DijkstraPathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        drawGraph(lines);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    private void drawGraph(List<Line> lines) {
        for (Line line : lines) {
            line.getSections().findAllStation().forEach(graph::addVertex);
            line.getSections().getSectionList().forEach(
                    section -> graph.setEdgeWeight(
                            graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()
                    )
            );
        }
    }

    @Override
    public Path findShortPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
        GraphPath<Station, DefaultWeightedEdge> path;
        try {
            path = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
        int charge = calculateCharge((int) path.getWeight());
        return new Path(path.getVertexList(), (int) path.getWeight(), charge);
    }

    @Override
    public int calculateCharge(int distance) {
        int fare = 1250;
        if (distance > 50) {
            int over50 = distance - 50;
            fare += ((over50 + 8 - 1) / 8) * 100;
            distance = 50;
        }

        if (distance > 10) {
            int over10 = distance -10;
            fare += ((over10 + 5 - 1) / 5) * 100;
        }
        return fare;
    }
}
