package subway.learning;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Disabled
@DisplayName("JGraph 학습 테스트")
class JGraphTest {

    @DisplayName("학습 1")
    @Test
    void JGraphTest1() {
        // given
        Station station1 = new Station(1L, "역1");
        Station station2 = new Station(2L, "역2");
        Station station3 = new Station(3L, "역3");

        Line line1 = new Line(1L, "line1", "blue");
        Line line2 = new Line(2L, "line2", "green");

        Section section1 = new Section(1L, line1, station1, station2, new Distance(10L));
        Section section2 = new Section(2L, line1, station2, station3, new Distance(5L));
        Section section3 = new Section(3L, line2, station1, station3, new Distance(100L));

        // when
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(station1);
        graph.addVertex(station2);
        graph.addVertex(station2);
        graph.addVertex(station3);

        graph.setEdgeWeight(graph.addEdge(section1.getUpStation(), section1.getDownStation()),
                section1.getDistance());
        graph.setEdgeWeight(graph.addEdge(section2.getUpStation(), section2.getDownStation()),
                section2.getDistance());
        graph.setEdgeWeight(graph.addEdge(section3.getUpStation(), section3.getDownStation()),
                section3.getDistance());

        GraphPath<Station, DefaultWeightedEdge> path
                = new DijkstraShortestPath<>(graph).getPath(station1, station3);

        // then
        List<Station> stations = path.getVertexList();
        Long distance = Math.round(path.getWeight());

        assertThat(stations).contains(station1, station2, station3);
        assertThat(distance).isEqualTo(15L);
    }

    @DisplayName("학습 2")
    @Test
    void JGraphTest2() {
        // given
        Station station1 = new Station(1L, "역1");
        Station station2 = new Station(2L, "역2");
        Station station3 = new Station(3L, "역3");

        Line line1 = new Line(1L, "line1", "blue");
        Line line2 = new Line(2L, "line2", "green");

        Section section1 = new Section(1L, line1, station1, station2, new Distance(10L));
        Section section2 = new Section(2L, line1, station2, station3, new Distance(5L));
        Section section3 = new Section(3L, line2, station1, station3, new Distance(100L));
        Section section4 = new Section(4L, line2, station2, station1, new Distance(100L));

        List<Section> allSections = List.of(section1, section2, section3, section4);

        // when
        Map<Long, Station> stationMap = new HashMap<>();
        WeightedMultigraph<Long, DefaultWeightedEdge> graph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allSections.forEach(section -> {
            stationMap.put(section.getUpStationId(), section.getUpStation());
            stationMap.put(section.getDownStationId(), section.getDownStation());
            graph.addVertex(section.getUpStationId());
            graph.addVertex(section.getDownStationId());
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance());
        });

        GraphPath<Long, DefaultWeightedEdge> path
                = new DijkstraShortestPath<>(graph).getPath(station1.getId(), station3.getId());

        // then
        List<Station> stations = path.getVertexList().stream()
                .map(stationMap::get)
                .collect(Collectors.toUnmodifiableList());
        Long distance = Math.round(path.getWeight());

        assertThat(stations).contains(station1, station2, station3);
        assertThat(distance).isEqualTo(15L);
    }

    @DisplayName("학습 3")
    @Test
    void JGraphTest3() {
        // given
        Station station1 = new Station(1L, "역1");
        Station station2 = new Station(2L, "역2");
        Station station3 = new Station(3L, "역3");

        Line line1 = new Line(1L, "line1", "blue");
        Line line2 = new Line(2L, "line2", "green");

        Section section1 = new Section(1L, line1, station1, station2, new Distance(10L));
        Section section2 = new Section(2L, line1, station2, station3, new Distance(5L));
        Section section3 = new Section(3L, line2, station1, station3, new Distance(100L));

        List<Section> allSections = List.of(section1, section2, section3);

        // when
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allSections.forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance());
        });

        GraphPath<Station, DefaultWeightedEdge> path
                = new DijkstraShortestPath<>(graph).getPath(station1, station3);

        // then
        List<Station> stations = path.getVertexList();
        Long distance = Math.round(path.getWeight());

        assertThat(stations).contains(station1, station2, station3);
        assertThat(distance).isEqualTo(15L);
    }
}
