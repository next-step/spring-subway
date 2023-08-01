package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.FindPathResponse;
import subway.exception.PathException;
import subway.exception.StationException;
import subway.vo.Path;

import java.text.MessageFormat;
import java.util.List;

@Service
public class PathService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final PathFinder pathFinder;

    public PathService(SectionDao sectionDao, StationDao stationDao, PathFinder pathFinder) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.pathFinder = pathFinder;
    }

    public FindPathResponse findPath(Long source, Long target) {
        Station startStation = getStation(source);
        Station endStation = getStation(target);
        validateSameStations(startStation, endStation);
        List<Section> sections = sectionDao.findAll();

        Path path = pathFinder.findPath(sections, startStation, endStation);

        return FindPathResponse.from(path.getStations(), path.getDistance());
    }

    private Station getStation(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new StationException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다.", stationId)
                )
        );
    }

    private void validateSameStations(Station startStation, Station endStation) {
        if (startStation.equals(endStation)) {
            throw new PathException("startStation과 endStation이 동일합니다");
        }
    }
}
