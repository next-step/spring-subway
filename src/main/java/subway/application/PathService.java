package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findShortestPath(Long departureStationId, Long destinationStationId) {
        Station departureStation = stationDao.findById(departureStationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
        Station destinationStation = stationDao.findById(destinationStationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
        List<Section> sections = sectionDao.findAll();

        PathFinder pathFinder = new PathFinder(sections, departureStation, destinationStation);
        List<StationResponse> stations = pathFinder.findShortestStations().stream()
            .map(StationResponse::of)
            .collect(Collectors.toUnmodifiableList());
        Double distance = pathFinder.findShortestDistance();
        return new PathResponse(stations, distance);
    }
}
