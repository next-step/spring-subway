package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.infrastrucure.SectionDao;
import subway.infrastrucure.StationDao;
import subway.domain.Fare;
import subway.domain.ShortestPath;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.station.StationNotFoundException;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = true)
    public PathResponse searchPaths(Long sourceStationId, Long targetStationId, Integer age) {
        List<Station> stations = stationDao.findAll();
        Station sourceStation = findStation(stations, sourceStationId);
        Station targetStation = findStation(stations, targetStationId);
        ShortestPath shortestPath = new ShortestPath(stations, sectionDao.findAll());
        int distance = shortestPath.getDistance(sourceStation, targetStation);
        return PathResponse.of(
            shortestPath.getPaths(sourceStation, targetStation),
            distance,
            new Fare(age, distance));
    }

    private Station findStation(List<Station> stations, Long stationId) {
        return stations.stream()
            .filter(station -> station.isSameId(stationId))
            .findFirst()
            .orElseThrow(StationNotFoundException::new);
    }
}
