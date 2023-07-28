package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import subway.dto.PathResponse;


public class Path {

    private final PathGraph graph;


    public Path(final Sections sections) {
        graph = new PathGraph(sections);
    }

    public Path(final List<Section> sections) {
        this(new Sections(sections));
    }

    public PathResponse createPath(final Station start, final Station end) {
        GraphPath<Station, DefaultWeightedEdge> route = graph.createRoute(start, end);
        return PathResponse.of(route);
    }
}
