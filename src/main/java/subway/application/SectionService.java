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

        sections.findConnectedSection(searchParams)
            .ifPresent(sectionDao::update);

        return SectionResponse.of(sectionDao.insert(searchParams));
    }

    public void deleteSection(final long lineId, final long stationId) {
        validateLineAndLastStation(lineId, stationId);
        validateSectionInLine(lineId);

        sectionDao.deleteLastSection(lineId, stationId);
    }

    private void validateLineAndLastStation(final long lineId, final long stationId) {
        final Section lastSection = sectionDao.findLastSection(lineId)
                .orElseThrow(() -> new IllegalSectionException("해당 노선은 생성되지 않았습니다."));

        if (!lastSection.matchDownStationId(stationId)) {
            throw new IllegalSectionException("해당 역은 노선의 하행 종점역이 아닙니다.");
        }
    }

    private void validateSectionInLine(final long lineId) {
        final long sectionCount = sectionDao.count(lineId);
        if (sectionCount == 1L) {
            throw new IllegalSectionException("해당 노선은 구간이 한개입니다.");
        }
    }
}
