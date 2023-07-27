package subway.jgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JGraphTest {

    @Test
    @DisplayName("JGraph 학습 테스트")
    void jGraph() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath dijkstraShortestPath
            = new DijkstraShortestPath(graph);
        List<String> shortestPath
            = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

        double pathWeight = dijkstraShortestPath.getPathWeight("v3", "v1");

        assertThat(shortestPath.size()).isEqualTo(3);
        assertThat(pathWeight).isEqualTo(4.0);
    }

    @Test
    @DisplayName("JGraph 학습 테스트: 출발지와 도착지가 연결되지 않은 경우")
    void jGraphException() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addVertex("v5");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v4", "v5"), 2);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        assertThatThrownBy(() -> dijkstraShortestPath.getPath("v4", "v1").getVertexList())
            .isInstanceOf(NullPointerException.class);
    }
}
