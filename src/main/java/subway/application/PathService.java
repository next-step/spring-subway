package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.ShortestPath;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.StationNotFoundException;

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
        List<Station> stations = stationDao.findAll();
        Station sourceStation = findStation(stations, sourceStationId);
        Station targetStation = findStation(stations, targetStationId);
        ShortestPath shortestPath = new ShortestPath(stations, sectionDao.findAll());
        return PathResponse.of(shortestPath.getPaths(sourceStation, targetStation),
            shortestPath.getDistance(sourceStation, targetStation));
    }

    private Station findStation(List<Station> stations, Long stationId) {
        return stations.stream()
            .filter(station -> station.isSameId(stationId))
            .findFirst()
            .orElseThrow(StationNotFoundException::new);
    }
}
