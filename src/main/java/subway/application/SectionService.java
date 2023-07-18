package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.request.SectionRegistRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public void registSection(SectionRegistRequest sectionRegistRequest, Long lineId) {
        Station upStation = stationDao.findById(sectionRegistRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRegistRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        Section section = new Section(
            upStation,
            downStation,
            line,
            sectionRegistRequest.getDistance()
        );

        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.validNewSection(section);

        sectionDao.insert(section);
    }

    public void deleteSection(Long stationId, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.canDeleteStation(stationId);

        sectionDao.deleteByDownStationIdAndLineId(stationId, lineId);
    }
}
