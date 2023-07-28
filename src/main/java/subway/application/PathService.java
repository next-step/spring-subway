package subway.application;

import org.springframework.stereotype.Service;
import subway.application.dto.ShortestPath;
import subway.dao.SectionDao;
import subway.domain.PathFinder;
import subway.ui.dto.PathResponse;

@Service
public class PathService {

    private final SectionDao sectionDao;

    public PathService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public PathResponse findShortestPaths(long source, long target) {
        PathFinder pathFinder = new PathFinder(sectionDao.findAll());
        ShortestPath shortestPath = pathFinder.searchShortestPath(source, target);
        return new PathResponse(
            shortestPath.getDistance(),
            shortestPath.getStations()
        );
    }
}
