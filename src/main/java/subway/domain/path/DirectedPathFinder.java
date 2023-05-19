package subway.domain.path;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

public class DirectedPathFinder {

    private static final int FIRST_INDEX = 0;

    private final Graph<Station, DefaultEdge> graph;
    private final AllDirectedPaths<Station, DefaultEdge> paths;

    private DirectedPathFinder() {
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        this.paths = new AllDirectedPaths<>(graph);
    }

    public static DirectedPathFinder of(Sections sections) {
        DirectedPathFinder pathFinder = new DirectedPathFinder();
        List<Section> sectionsValue = sections.getValue();
        sectionsValue.forEach(pathFinder::initGraph);
        return pathFinder;
    }

    private void initGraph(Section section) {
        Station downStation = section.getDownStation();
        Station upStation = section.getUpStation();

        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.addEdge(upStation, downStation);
    }

    public List<Station> getPath(Station source, Station destination) {
        List<GraphPath<Station, DefaultEdge>> allPaths = paths.getAllPaths(source, destination, true, null);

        if (allPaths.isEmpty()) {
            throw new ServiceException(ErrorType.INVALID_PATH);
        }

        List<Station> orderedStations = new ArrayList<>();
        for (Station station : allPaths.get(FIRST_INDEX).getVertexList()) {
            orderedStations.add(station);
        }

        return orderedStations;
    }

}
