package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@Service
public class PathService {

    private final SectionDao sectionDao;

    public PathService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public PathResponse findMinimumDistancePaths(Long departureStationId, Long destinationStationId) {
        List<Section> sections = sectionDao.findAll();
        PathFinder pathFinder = new PathFinder(sections);

        List<StationResponse> stations = pathFinder.findStations(departureStationId, destinationStationId).stream()
            .map(StationResponse::of)
            .collect(Collectors.toUnmodifiableList());
        Long distance = pathFinder.findMinimumDistance(departureStationId, destinationStationId);

        return new PathResponse(stations, distance);
    }
}
