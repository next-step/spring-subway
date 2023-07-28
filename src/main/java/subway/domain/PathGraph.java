package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;


    public PathGraph(List<Section> sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        sections.forEach(section -> graph.addVertex(section.getDownStation()));
        sections.forEach(section -> graph.addVertex(section.getUpStation()));
        sections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()),
                section.getDistance()
            )
        );
    }

    public GraphPath<Station, DefaultWeightedEdge> createRoute(Station source, Station sink) {
        return new DijkstraShortestPath<>(graph).getPath(source, sink);
    }

}