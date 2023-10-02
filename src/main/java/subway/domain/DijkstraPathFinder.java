package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class DijkstraPathFinder implements PathFinder {

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
            line.getSections().getSections().forEach(
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
        List<Station> vertexList = path.getVertexList();
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < vertexList.size(); i++) {
            for (int j = i+1; j < vertexList.size(); j++) {
                sections.add(new Section(vertexList.get(i), vertexList.get(j), (int) path.getWeight()));
            }
        }
        return new Path(new Sections(sections));
    }

}
