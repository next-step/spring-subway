package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.ConnectedSections;
import subway.domain.Section;
import subway.domain.SectionEditResult;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final Long lineId, final SectionRequest sectionRequest) {
        final ConnectedSections sections = new ConnectedSections(sectionDao.findAllByLineId(lineId));

        final Section section = sectionRequest.toSection(lineId);
        final SectionEditResult editResult = sections.add(section);

        editResult.getAddedSections().forEach(sectionDao::insert);

        return SectionResponse.of(section);
    }

    public void deleteSection(final Long lineId, final Long downStationId) {
        final ConnectedSections sections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final SectionEditResult editResult = sections.remove(downStationId);

        editResult.getRemovedSections().forEach(section -> sectionDao.delete(section.getId()));
    }
}
