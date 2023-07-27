package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

class JGraphtTest {

    // TODO: 리팩토링하기
    private static final Station 강남역 = new Station(20L, "강남역");
    private static final Station 교대역 = new Station(21L, "교대역");
    private static final Station 양재역 = new Station(30L, "양재역");
    private static final Station 남부터미널역 = new Station(31L, "남부터미널역");
    private static final Line 이호선 = new Line(2L, "2호선", "#000001");
    private static final Line 삼호선 = new Line(3L, "3호선", "#000002");
    private static final Line 신분당선 = new Line(4L, "신분당선", "#000003");
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
