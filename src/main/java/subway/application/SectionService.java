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
import subway.domain.vo.SectionRegisterVo;
import subway.dto.request.SectionRegisterRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void registerSection(SectionRegisterRequest sectionRegisterRequest, Long lineId) {
        Station upStation = stationDao.findById(sectionRegisterRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRegisterRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        Section section = new Section(
            upStation,
            downStation,
            line,
            sectionRegisterRequest.getDistance()
        );

        Sections sections = sectionDao.findAllByLineId(lineId);
        SectionRegisterVo result = sections.registerSection(section);

        sectionDao.insert(result.getAddSection());

        if (result.getUpdateSection().isPresent()) {
            sectionDao.update(result.getUpdateSection().get());
        }
    }

    @Transactional
    public void deleteSection(Long stationId, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.canDeleteStation(stationId);

        sectionDao.deleteByDownStationIdAndLineId(stationId, lineId);
    }
}
