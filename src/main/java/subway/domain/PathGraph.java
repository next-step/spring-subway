package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.List;
import java.util.stream.Stream;

public class PathGraph {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private PathGraph(final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static PathGraph of(final List<Section> sections) {
        validateSize(sections);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(sections.get(0).getUpStation());
        sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .forEach(graph::addVertex);

        sections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
        ));
        return new PathGraph(graph);
    }

    private static void validateSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SubwayException(ErrorCode.PATH_NO_SECTIONS);
        }
    }

    public Path findShortestPath(final Station sourceStation, final Station targetStation) {
        validateSameStations(sourceStation, targetStation);
        validateContains(sourceStation);
        validateContains(targetStation);

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph)
                .getPath(sourceStation, targetStation);

        validatePath(path);

        return new Path(path.getVertexList(), (int) path.getWeight());
    }

    private void validatePath(GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new SubwayException(ErrorCode.NO_PATH);
        }
    }

    private void validateContains(Station station) {
        if (!graph.containsVertex(station)) {
            throw new SubwayException(ErrorCode.STATION_NOT_CONTAINED, station.getId());
        }
    }

    private void validateSameStations(final Station sourceStation, final Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new SubwayException(ErrorCode.PATH_SAME_STATIONS);
        }
    }
}
