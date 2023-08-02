package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.StationNotFoundException;

@Service
public class PathService {

    private final SectionDao sectionDao;

    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findPath(Long source, Long target) {
        List<Section> allSections = sectionDao.findAll();
        Station sourceStation = stationDao.findById(source)
                .orElseThrow(() -> new StationNotFoundException(source));
        Station targetStation = stationDao.findById(target)
                .orElseThrow(() -> new StationNotFoundException(target));

        Path path = Path.of(allSections, sourceStation, targetStation);
        return PathResponse.of(path);
    }
}
