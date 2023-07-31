package subway.application;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.domain.Section;
import subway.domain.ShortestPathFinder;
import subway.domain.Station;

public class DefaultShortestPathFinder implements ShortestPathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private List<Station> stations;
    private int distance;

    @Override
    public void calculatePath(final List<Section> sections, final Station source, final Station target) {
        makeGraph(sections, source, target);

        final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);

        validateConnection(path);
        setResult(path);
    }

    private void setResult(final GraphPath<Station, DefaultWeightedEdge> path) {
        this.stations = path.getVertexList();
        final double distance = path.getWeight();
        validateRange(distance);
        this.distance = (int) distance;
    }

    private void makeGraph(final List<Section> sections, final Station source,
        final Station target) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initializeVertex(sections, source, target);
        initializeEdge(sections);
    }


    private void initializeVertex(final List<Section> sections, final Station source, final Station target) {
        sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .forEach(graph::addVertex);

        graph.addVertex(source);
        graph.addVertex(target);
    }

    private void initializeEdge(final List<Section> sections) {
        sections.forEach(
            section -> graph.setEdgeWeight(addEdge(section), section.getDistance()));
    }

    private DefaultWeightedEdge addEdge(final Section section) {
        return graph.addEdge(section.getUpStation(), section.getDownStation());
    }

    private void validateConnection(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다");
        }
    }

    private void validateRange(final double distance) {
        if (distance > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("경로 길이가 허용 범위를 초과합니다.");
        }
    }

    @Override
    public List<Station> getStations() {
        return stations;
    }

    @Override
    public int getDistance() {
        return distance;
    }
}
