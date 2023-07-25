package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.IllegalSectionException;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionResponse saveSection(final long lineId, final SectionRequest sectionRequest) {
        final Sections sections = new Sections(sectionDao.findAll(lineId));
        final Section searchParams = sectionRequest.to(lineId);

        if (sections.hasConnectedSection(searchParams)) {
            Section update = sections.findConnectedSection(searchParams);
            sectionDao.update(update);
        }

        return SectionResponse.of(sectionDao.insert(searchParams));
    }

    public void deleteSection(final long lineId, final long stationId) {
        validateLineInStation(lineId, stationId);
        validateSectionInLine(lineId);

        final Sections sections = new Sections(sectionDao.findAll(lineId));

        if (sections.isLastStation(stationId)) {
            deleteLastSection(stationId, sections);
            return;
        }

        deleteInnerStation(stationId, sections);
    }

    private void deleteInnerStation(long stationId, Sections sections) {
        final Section upDirection = sections.findUpDirectionSection(stationId);
        final Section downDirection = sections.findDownDirectionSection(stationId);
        final Section extendedSection = downDirection.extendToUpDirection(upDirection);

        sectionDao.delete(upDirection.getId());
        sectionDao.update(extendedSection);
    }

    private void deleteLastSection(long stationId, Sections sections) {
        final Section connectedSection = sections.getLastSection(stationId);
        sectionDao.delete(connectedSection.getId());
    }

    private void validateLineInStation(final long lineId, final long stationId) {
        if (!sectionDao.existByLineIdAndStationId(lineId, stationId)) {
            throw new IllegalSectionException("해당 역은 노선에 존재하지 않습니다.");
        }
    }

    private void validateSectionInLine(final long lineId) {
        final long sectionCount = sectionDao.count(lineId);
        if (sectionCount == 1L) {
            throw new IllegalSectionException("해당 노선은 구간이 한개입니다.");
        }
    }
}
