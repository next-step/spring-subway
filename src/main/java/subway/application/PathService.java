package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.ShortestPath;
import subway.domain.Station;
import subway.dto.PathResponse;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = true)
    public PathResponse searchPaths(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationDao.findById(sourceStationId);
        Station targetStation = stationDao.findById(targetStationId);
        ShortestPath shortestPath = new ShortestPath(stationDao.findAll(), sectionDao.findAll());
        return PathResponse.of(shortestPath.getPaths(sourceStation, targetStation),
            shortestPath.getDistance(sourceStation, targetStation));
    }
}
