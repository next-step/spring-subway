package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

class JGraphtTest {

    private static final Station 강남역 = new Station(20L, "강남역");
    private static final Station 교대역 = new Station(21L, "교대역");
    private static final Station 양재역 = new Station(30L, "양재역");
    private static final Station 남부터미널역 = new Station(31L, "남부터미널역");
    private static final Section 강남역_교대역_구간 = new Section(1L, 2L, 20L, 21L, 130L);
    private static final Section 강남역_양재역_구간 = new Section(2L, 4L, 20L, 30L, 30L);
    private static final Section 양재역_남부터미널역_구간 = new Section(3L, 3L, 30L, 31L, 30L);
    private static final Section 남부터미널역_교대역_구간 = new Section(4L, 3L, 31L, 21L, 30L);
    private static final List<Section> SECTIONS = List.of(강남역_교대역_구간, 강남역_양재역_구간, 양재역_남부터미널역_구간, 남부터미널역_교대역_구간);

    @Test
    void getDijkstraShortestPath() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

        assertThat(shortestPath).containsExactly("v3", "v2", "v1");
    }

    @Test
    @DisplayName("Source와 Target이 연결되지 않는 경우 최단 경로는 null을 반환한다.")
    void getDijkstraShortestPathWithNotConnectedSourceAndTarget() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 2);

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<String, DefaultWeightedEdge> path = dijkstraShortestPath.getPath("v1", "v4");

        assertThat(path).isNull();
    }

    @Test
    @DisplayName("Source와 Target이 연결되지 않는 경우 최단 경로의 weight는 infinite를 반환한다.")
    void getDijkstraShortestPathWeightWithNotConnectedSourceAndTarget() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 2);

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final double pathWeight = dijkstraShortestPath.getPathWeight("v1", "v4");

        assertThat(pathWeight).isInfinite();
    }

    @Test
    @DisplayName("역의 ID와 구간으로 다익스트라 알고리즘 테스트")
    void getDijkstraShortestPathWithStationAndSection() {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(강남역.getId());
        graph.addVertex(교대역.getId());
        graph.addVertex(양재역.getId());
        graph.addVertex(남부터미널역.getId());
        for (Section section : SECTIONS) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance());
        }

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<Long> shortestPath = dijkstraShortestPath.getPath(강남역.getId(), 교대역.getId()).getVertexList();

        assertThat(shortestPath).containsExactly(20L, 30L, 31L, 21L);
    }

    @Test
    @DisplayName("역의 ID와 구간으로 다익스트라 알고리즘 경로의 비용 테스트")
    void getDijkstraShortestPathWeightWithStationAndSection() {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(강남역.getId());
        graph.addVertex(교대역.getId());
        graph.addVertex(양재역.getId());
        graph.addVertex(남부터미널역.getId());
        for (Section section : SECTIONS) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance());
        }

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final long pathWeight = (long) dijkstraShortestPath.getPathWeight(강남역.getId(), 교대역.getId());

        assertThat(pathWeight).isEqualTo(90);
    }
}
