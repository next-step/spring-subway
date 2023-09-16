package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.*;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathFindService {

    private final StationService stationService;
    private final LineDao lineDao;

    public PathFindService(StationService stationService, LineDao lineDao) {
        this.stationService = stationService;
        this.lineDao = lineDao;
    }

    public PathResponse findShortPath(Long sourceId, Long targetId) {
        Station startStation = stationService.findStationById(sourceId);
        Station endStation = stationService.findStationById(targetId);

        List<Line> lines = lineDao.findAll();

        PathFinder pathFinder = new DijkstraPathFinder(lines);
        return createPathResponse(pathFinder.findShortPath(startStation, endStation));
    }

    private PathResponse createPathResponse(Path path) {
        return new PathResponse(createStationResponse(path.getStations()), path.getDistance(), path.getCharge());
    }

    public List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream().map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList());
    }


}
