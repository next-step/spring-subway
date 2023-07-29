package subway.study;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Jgraph 라이브러리")
public class JgraphtTest {

    @Test
    @DisplayName("노드와 노드간의 최단 경로와 거리를 계산한다")
    void getShortestPath() {
        // given
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");

        // when
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 1);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 1);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath<String, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> graphPath = shortestPath.getPath("v1", "v3");

        List<String> path = graphPath.getVertexList();
        double distance = shortestPath.getPathWeight("v1", "v3");

        // then
        assertThat(path.size()).isEqualTo(3);
        assertThat(path).containsExactly("v1", "v2", "v3");
        assertThat(distance).isEqualTo(2);

    }

    @Test
    @DisplayName("노드와 노드가 연결되지 않을 경우 path는 null 이다")
    void returnNullWhenNotLinked() {
        // given
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // when
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 1);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);

        DijkstraShortestPath shortestPath = new DijkstraShortestPath(graph);

        GraphPath<String, DefaultWeightedEdge> graphPath = shortestPath.getPath("v1", "v4");

        // then
        assertThat(graphPath).isNull();

    }
}
