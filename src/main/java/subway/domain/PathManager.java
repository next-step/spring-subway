package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public final class PathManager {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap;

    public PathManager(final WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap) {
        this.subwayMap = subwayMap;
    }

    public static PathManager create(final List<Station> stations, final List<Section> sections) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(subwayMap::addVertex);
        sections.forEach(section -> subwayMap.setEdgeWeight(
                subwayMap.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
        ));
        return new PathManager(subwayMap);
    }

    public List<Station> findStationsOfShortestPath(final Station source, final Station target) {
        final GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath().getPath(source, target);
        return shortestPath.getVertexList();
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath() {
        return new DijkstraShortestPath<>(subwayMap);
    }
}
