package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.PathGraph;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

@Service
public class PathService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationDao.findById(sourceStationId)
                .orElseThrow(() -> new SubwayException(ErrorCode.STATION_ID_NO_EXIST, sourceStationId));
        Station targetStation = stationDao.findById(targetStationId)
                .orElseThrow(() -> new SubwayException(ErrorCode.STATION_ID_NO_EXIST, targetStationId));

        PathGraph graph = PathGraph.of(sectionDao.findAll());

        return PathResponse.of(graph.findShortestPath(sourceStation, targetStation));
    }
}
