package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;

public class GraphTest {

    public static WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(
        List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);

        sections.forEach(section -> graph.addVertex(section.getDownStation()));
        sections.forEach(section -> graph.addVertex(section.getUpStation()));
        sections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()),
                section.getDistance()
            )
        );
        return graph;
    }

    @Test
    @DisplayName("Route를 생성한다")
    void graph_생성_테스트() {
        // given
        Station 오이도역 = new Station("오이도역");
        Station 서울역 = new Station("서울역");
        Station 사당역 = new Station("사당역");

        Section section = new Section(서울역, 오이도역, new Distance(10));
        Section section2 = new Section(서울역, 사당역, new Distance(5));

        // when
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(
            List.of(section, section2)
        );
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(
            graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraAlg.getPath(오이도역, 사당역);

        // then
        assertThat(path.getWeight()).isEqualTo(15);
        assertThat(path.getVertexList()).containsAll(List.of(오이도역, 사당역, 서울역));
    }
}
