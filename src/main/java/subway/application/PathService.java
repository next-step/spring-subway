package subway.application;

import static subway.exception.ErrorCode.NOT_FOUND_SECTION;
import static subway.exception.ErrorCode.NOT_FOUND_STATION;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public PathResponse createPath(final Long sourceId, final Long targetId) {

        List<Section> sections = sectionDao.findAll()
            .orElseThrow(() -> new SubwayException(NOT_FOUND_SECTION));
        Path path = new Path(new Sections(sections));

        Station startStation = stationDao.findById(sourceId)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station endStation = stationDao.findById(targetId)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));

        PathResponse pathResponse = path.createPath(startStation, endStation);

        return pathResponse;
    }
}
