package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

public class JgraphtTest {

    private Station chungjeong = new Station(1L, "충정로");
    private Station sichung = new Station(2L, "시청");
    private Station uljiro3 = new Station(3L, "을지로 3가");
    private Station seoulyuk = new Station(4L, "서울역");
    private Station jongro3 = new Station(5L, "종로 3가");
    private Station uljiro4 = new Station(6L, "을지로 4가");


    @Test
    @DisplayName("다익스트라 최단거리 테스트")
    void getDijkstraShortestPath() {
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

        assertThat(shortestPath.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Station 노드로 버전 다익스트라 최단거리 구하기")
    // vertex -> station
    // edge -> section
    void getDijkstraShortestPathWithStationNode() {
        // given
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        // vertex (station)
        graph.addVertex(chungjeong);
        graph.addVertex(sichung);
        graph.addVertex(uljiro3);
        graph.addVertex(seoulyuk);
        graph.addVertex(jongro3);
        graph.addVertex(uljiro4);

        // edge (section)
        graph.setEdgeWeight(graph.addEdge(chungjeong, sichung), 1);
        graph.setEdgeWeight(graph.addEdge(sichung, uljiro3), 1);
        graph.setEdgeWeight(graph.addEdge(uljiro3, uljiro4), 10);
        graph.setEdgeWeight(graph.addEdge(seoulyuk, sichung), 2);
        graph.setEdgeWeight(graph.addEdge(sichung, jongro3), 2);
        graph.setEdgeWeight(graph.addEdge(jongro3, uljiro4), 2);

        // when
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(sichung, uljiro4);
        List<Station> shortenPath = graphPath.getVertexList();
        double shortenDistance = graphPath.getWeight();

        // then
        List<Station> answer = List.of(sichung, jongro3, uljiro4);

        System.out.println(graph);
        assertThat(shortenPath).isEqualTo(answer);
        assertThat(shortenDistance).isEqualTo(4);
    }

    @Test
    @DisplayName("thread-safe 그래프 vertex add 테스트")
    void threadSafeGraphTest() throws InterruptedException {
        // given
        
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);
        AsSynchronizedGraph syncGraph = new AsSynchronizedGraph(graph);

        int numberOfRunner = 5;
        int numberOfCreation = 1000;

        // when
        List<Thread> myThreads = new ArrayList<>();
        for (int i = 0; i < numberOfRunner; i++) {
            Thread thread = new Thread(new AddVerTexRunner(i, numberOfCreation, syncGraph));
            thread.start();
            myThreads.add(thread);
        }
        for (Thread th: myThreads) {
            th.join();
        }

        // then
        assertThat(syncGraph.vertexSet().size()).isEqualTo(numberOfRunner * numberOfCreation);
    }
}

class AddVerTexRunner implements Runnable {
    private int runnerIndex;
    private int numberOfCreation;
    private AsSynchronizedGraph syncGraph;

    public AddVerTexRunner(int runnerIndex, int numberOfCreation, AsSynchronizedGraph syncGraph) {
        this.runnerIndex = runnerIndex;
        this.numberOfCreation = numberOfCreation;
        this.syncGraph = syncGraph;
    }
    @Override
    public void run() {
        for (int i = 0; i < numberOfCreation; i++) {
            long uniqId = ((long) i * numberOfCreation + runnerIndex);
            syncGraph.addVertex(new Station(uniqId, "종로"));
        }
    }
}
