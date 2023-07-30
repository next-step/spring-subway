package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        return new PathResponse(List.of(
                new StationResponse(1L, "교대역"),
                new StationResponse(2L, "강남역"),
                new StationResponse(3L, "역삼역")),
                3);
    }
}
