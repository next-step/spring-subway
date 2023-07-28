package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.DistinctSections;
import subway.domain.Station;
import subway.domain.StationGraph;
import subway.dto.PathRequest;
import subway.dto.PathResponse;

import java.util.List;

@Service
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findPath(final PathRequest pathRequest) {
        final Long sourceId = pathRequest.getSource();
        final Long targetId = pathRequest.getTarget();
        final DistinctSections sections = new DistinctSections(sectionDao.findAll());

        final StationGraph stationGraph = new StationGraph(sections);
        final List<Long> stationIds = stationGraph.getShortestPathStationIds(sourceId, targetId);
        final List<Station> stations = stationDao.findAllByIds(stationIds);

        return PathResponse.of(stations, stationGraph.getShortestPathDistance(sourceId, targetId));
    }
}
