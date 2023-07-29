package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathGraph;
import subway.domain.ShortPath;
import subway.domain.Station;
import subway.domain.WholeSection;
import subway.dto.request.PathFindRequest;
import subway.dto.response.PathFindResponse;

@Service
public class PathFinder {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathFinder(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = true)
    public PathFindResponse findShortPath(final PathFindRequest request) {
        final Station source = getStationById(request.getSource());
        final Station target = getStationById(request.getTarget());
        final WholeSection wholeSection = new WholeSection(sectionDao.findAll());
        final PathGraph pathGraph = new PathGraph(wholeSection);
        final ShortPath shortPath = pathGraph.getShortPath(source, target);
        return PathFindResponse.of(shortPath);
    }

    private Station getStationById(final Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("역을 찾을 수 없습니다."));
    }

}
