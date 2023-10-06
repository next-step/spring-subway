package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.*;
import subway.dto.PathResponse;
import subway.dto.StationResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathFindService {

    private final StationService stationService;
    private final LineService lineService;

    public PathFindService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findShortPath(Long sourceId, Long targetId) {
        Station startStation = stationService.findStationById(sourceId);
        Station endStation = stationService.findStationById(targetId);

        List<Line> lines = lineService.findLines();

        PathFinder pathFinder = new DijkstraPathFinder(lines);
        return createPathResponse(pathFinder.findShortPath(startStation, endStation));
    }

    private PathResponse createPathResponse(Path path) {
        int totalDistance = path.getTotalDistance();
        return new PathResponse(createStationResponse(path.getStations()), totalDistance, path.getCharge(totalDistance));
    }

    public List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream().map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList());
    }


}
