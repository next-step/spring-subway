package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.PathResponse;

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
                .orElseThrow();
        Station targetStation = stationDao.findById(targetStationId)
                .orElseThrow();

        Sections sections = new Sections(sectionDao.findAll());

        return PathResponse.of(sections.findShortestPath(sourceStation, targetStation));
    }
}
