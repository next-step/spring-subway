package subway.domain;

import org.springframework.stereotype.Component;
import subway.vo.Path;

import java.util.List;

@Component
public class PathFinder {

    public Path findPath(List<Section> sections, Station startStation, Station endStation) {
        PathFinderFacade pathFinderFacade = new PathFinderFacade(sections, startStation, endStation);
        return pathFinderFacade.findPath(startStation, endStation);
    }
}
