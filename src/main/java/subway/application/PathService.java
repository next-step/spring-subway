package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@Service
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findMinimumDistancePaths(Long departureStationId, Long destinationStationId) {
        List<Section> sections = sectionDao.findAll();
        Station departureStation = stationDao.findById(departureStationId);
        Station destinationStation = stationDao.findById(destinationStationId);

        PathFinder pathFinder = new PathFinder(sections, departureStation, destinationStation);
        List<StationResponse> stations = pathFinder.findShortestStations().stream()
            .map(StationResponse::of)
            .collect(Collectors.toUnmodifiableList());
        Double distance = pathFinder.findShortestDistance();
        return new PathResponse(stations, distance);
    }
}
