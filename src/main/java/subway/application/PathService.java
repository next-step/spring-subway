package subway.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Station;
import subway.dto.PathResponse;

@Service
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        Path path = new Path(sectionDao.findAll());
        List<Long> shortestPathStationIds = path.findShortestPath(sourceId, targetId);
        long shortestPathWeight = path.findShortestPathWeight(sourceId, targetId);

        final Map<Long, Station> stations = stationDao.findAllByStationIdIn(shortestPathStationIds).stream()
                .collect(Collectors.toMap(Station::getId, station -> station));

        return PathResponse.of(shortestPathStationIds, stations, shortestPathWeight);
    }
}
