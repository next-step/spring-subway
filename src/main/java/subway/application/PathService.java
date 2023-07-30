package subway.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathFinder;
import subway.domain.PathFinderResult;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.StationException;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        Station source = stationDao.findById(sourceId).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "구간을 찾기 위한 출발역이 존재하지 않습니다."));
        Station target = stationDao.findById(targetId).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "구간을 찾기 위한 도착역이 존재하지 않습니다."));
        List<Section> sections = sectionDao.findAll();

        PathFinder pathFinder = new PathFinder(sections);
        PathFinderResult result = pathFinder.findShortestPath(source, target);

        List<Station> unsortedStations = stationDao.findAllIn(result.getPaths());

        Map<Long, Station> idToStation = new HashMap<>();
        unsortedStations.forEach(station -> idToStation.put(station.getId(), station));

        List<StationResponse> stationsInOrder = new ArrayList<>();
        unsortedStations.forEach(station -> stationsInOrder.add(StationResponse.of(idToStation.get(station.getId()))));

        return new PathResponse(stationsInOrder, result.getDistance().getValue());
    }
}
