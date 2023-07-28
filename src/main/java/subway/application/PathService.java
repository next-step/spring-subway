package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.ui.dto.PathResponse;

@Service
public class PathService {

    private final SectionDao sectionDao;

    public PathService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public PathResponse findShortestPaths(long source, long target) {
        List<Section> allSections = sectionDao.findAll();
        PathFinder pathFinder = new PathFinder(allSections);
        return pathFinder.searchShortestPath(source, target);
    }
}
