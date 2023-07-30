package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathManager;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

import java.util.List;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(final Long sourceId, final Long targetId) {
        final PathManager pathManager = PathManager.create(
                stationDao.findAll(), sectionDao.findAll()
        );

        final Station source = findStation(sourceId);
        final Station target = findStation(targetId);

        final List<Station> stationsOfShortestPath = pathManager.findStationsOfShortestPath(source, target);
        final double distanceOfShortestPath = pathManager.findDistanceOfShortestPath(source, target);

        return PathResponse.of(stationsOfShortestPath, distanceOfShortestPath);
    }

    private Station findStation(final long id) {
        return stationDao.findById(id)
                .orElseThrow(() ->
                        new SubwayException(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", id))
                );
    }
}
