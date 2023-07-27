package subway.application;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.exception.StatusCodeException;
import subway.domain.response.PathResponse;
import subway.dto.PathFindResponse;

@Service
public class PathService {

    private static final String CANNOT_FIND_STATION = "PATH-SERVICE-401";

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = true)
    public PathFindResponse getMinimumPath(long sourceStationId, long targetStationId) {
        Station sourceStation = getStation(sourceStationId);
        Station targetStation = getStation(targetStationId);

        List<Section> sections = sectionDao.findAll();
        Path path = new Path(sections);
        PathResponse pathResponse = path.minimumPath(sourceStation, targetStation);

        return new PathFindResponse(pathResponse.getStations().stream()
                .map(stationResponse -> new PathFindResponse.StationResponse(stationResponse.getId(),
                        stationResponse.getName()))
                .collect(Collectors.toList()), pathResponse.getDistance());
    }

    private Station getStation(long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new StatusCodeException(
                MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다", stationId), CANNOT_FIND_STATION));
    }

}
