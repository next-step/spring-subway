package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionRequest;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void saveSection(final Long lineId, final SectionRequest request) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Line line = lineDao.findById(lineId).addSections(sections);
        Station upward = stationDao.findById(request.getUpStationId());
        Station downward = stationDao.findById(request.getDownStationId());
        Section section = new Section(upward, downward, line, request.getDistance());

        line.addSection(section);

        sectionDao.insert(section);
    }
}
