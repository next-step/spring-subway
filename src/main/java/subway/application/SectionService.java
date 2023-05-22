package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.SectionAddRequest;
import subway.dto.SectionResponse;

@Service
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
    public SectionResponse add(Long lineId, SectionAddRequest sectionAddRequest) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section section = sectionAddRequest.toSection(lineDao.findById(lineId),
            stationDao.findById(sectionAddRequest.getUpStationId()),
            stationDao.findById(sectionAddRequest.getDownStationId()));
        sections.addSection(section);
        return SectionResponse.from(sectionDao.insert(section));
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.removeLastSection(stationId);
        sectionDao.delete(lineId, stationId);
    }
}
