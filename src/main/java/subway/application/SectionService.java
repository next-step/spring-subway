package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.ConnectedSections;
import subway.domain.Section;
import subway.domain.SectionEditResult;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.util.stream.Collectors;

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

        dirtyChecking(editResult);

        return SectionResponse.of(section);
    }

    public void deleteSection(final Long lineId, final Long downStationId) {
        final ConnectedSections sections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final SectionEditResult editResult = sections.remove(downStationId);

        dirtyChecking(editResult);
    }

    private void dirtyChecking(final SectionEditResult sectionEditResult) {
        sectionDao.insertAll(sectionEditResult.getAddedSections());
        sectionDao.deleteAll(sectionEditResult.getRemovedSections().stream()
                .map(Section::getId)
                .collect(Collectors.toList()));
    }
}
