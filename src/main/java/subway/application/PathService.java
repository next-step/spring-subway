package subway.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.PathDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.PathFinder;
import subway.domain.PathFinderResult;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.StationException;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final PathDao pathDao;

    public PathService(final StationDao stationDao, final SectionDao sectionDao, final PathDao pathDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.pathDao = pathDao;
    }

    @Transactional
    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        Station source = stationDao.findById(sourceId).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "구간을 찾기 위한 출발역이 존재하지 않습니다."));
        Station target = stationDao.findById(targetId).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "구간을 찾기 위한 도착역이 존재하지 않습니다."));

        Optional<Path> optionalCachedPath = pathDao.findPathBySourceAndTarget(source, target);
        if (optionalCachedPath.isPresent()) {
            return loadCache(source, target, optionalCachedPath.get());
        }

        PathFinder pathFinder = new PathFinder(sectionDao.findAll());
        PathFinderResult result = pathFinder.findShortestPath(source, target);

        List<StationResponse> stationResponses = sortedStationResponse(result, stationDao.findAllIn(result.getPaths()));

        long pathId = pathDao.insertPathForCache(source, target, result.getDistance());
        List<Long> sortedStationIds = stationResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        pathDao.insertWaypointsForCache(pathId, sortedStationIds, target.hasSmallerIdThan(source));

        return new PathResponse(stationResponses, result.getDistance().getValue());
    }

    private List<StationResponse> sortedStationResponse(final PathFinderResult result,
            final List<Station> unsortedStations) {
        Map<Long, Station> idToStation = unsortedStations.stream()
                .collect(Collectors.toMap(Station::getId, station -> station));

        return result.getPaths().stream()
                .map(stationId -> StationResponse.of(idToStation.get(stationId)))
                .collect(Collectors.toList());
    }

    private PathResponse loadCache(final Station source, final Station target, final Path cachedPath) {
        List<Station> cachedWaypoints = pathDao.findWaypointsByPathId(
                cachedPath.getPathId(), target.hasSmallerIdThan(source));
        List<StationResponse> stationResponses = cachedWaypoints.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, cachedPath.getDistance().getValue());
    }
}
