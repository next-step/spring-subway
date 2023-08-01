package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

import java.util.List;

import static subway.exception.ErrorCode.NOT_FOUND_SECTION;
import static subway.exception.ErrorCode.NOT_FOUND_STATION;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public PathResponse createPath(final Long sourceId, final Long targetId) {
        Station startStation = stationDao.findById(sourceId)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station endStation = stationDao.findById(targetId)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));

        List<Section> sections = sectionDao.findAll()
            .orElseThrow(() -> new SubwayException(NOT_FOUND_SECTION));
        PathGraph pathGraph = new PathGraph(new Sections(sections))
            .createPath(startStation, endStation);
        Path path = new Path(pathGraph);
        return PathResponse.from(path);
    }
}
