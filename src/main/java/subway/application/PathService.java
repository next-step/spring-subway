package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineStationsResponse;
import subway.dto.PathResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PathService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse searchShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationDao.findById(sourceStationId);
        Station targetStation = stationDao.findById(targetStationId);

        List<Line> lines = lineDao.findAll();
        List<Sections> allSections = lines.stream()
                .map(Line::getId)
                .map(sectionDao::findAllByLineId)
                .collect(Collectors.toList());

        //Path path = new Path(allSections, sourceStation, targetStation);
        //return PathResponse(path.getPath(), path.getDistance());
        return null;
    }

}
