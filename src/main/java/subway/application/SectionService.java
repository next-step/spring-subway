package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.SectionAddRequest;
import subway.dto.SectionResponse;

@Transactional
@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse add(Long lineId, SectionAddRequest sectionAddRequest) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section section = sectionAddRequest.toSection(lineId);
        sections.addSection(section);
        return SectionResponse.from(sectionDao.insert(section));
    }

    public void remove(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.removeLastSection(stationId);
        sectionDao.delete(lineId, stationId);
    }
}
