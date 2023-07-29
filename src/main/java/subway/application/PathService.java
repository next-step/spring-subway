package subway.application;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

@Service
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        validateSameSourceAndTarget(sourceId, targetId);
        Path path = new Path(sectionDao.findAll());
        validatePathContainsSourceAndTarget(path, sourceId, targetId);

        List<Long> shortestPathStationIds = path.findShortestPathVertices(sourceId, targetId);
        long shortestPathWeight = path.findShortestPathWeight(sourceId, targetId);
        final Map<Long, Station> stations = stationDao.findAllByStationIdIn(shortestPathStationIds).stream()
                .collect(Collectors.toMap(Station::getId, station -> station));

        return PathResponse.of(shortestPathStationIds, stations, shortestPathWeight);
    }

    private void validatePathContainsSourceAndTarget(final Path path, final Long sourceId, final Long targetId) {
        if (!path.isPathHasVertex(sourceId, targetId)) {
            throw new SubwayException("출발역과 도착역이 해당 노선도에 등록되어 있지 않습니다. 출발역 ID : " + sourceId + " 도착역 ID : " + targetId);
        }
    }

    private void validateSameSourceAndTarget(final Long sourceId, final Long targetId) {
        if (Objects.equals(sourceId, targetId)) {
            throw new SubwayException("출발역과 도착역이 같습니다. 역 ID : " + sourceId);
        }
    }
}
