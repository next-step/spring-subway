package subway.application;

import org.springframework.stereotype.Service;
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

    public SectionResponse saveSection(final long lineId, final SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findAll(lineId));
        sections.updateForInsert(sectionRequest.to(lineId))
                .ifPresent(sectionDao::update);

        final Section section = sectionDao.insert(sectionRequest.to(lineId));
        return SectionResponse.of(section);
    }

    public void deleteSection(final long lineId, final long stationId) {
        validateLineAndLastStation(lineId, stationId);
        validateSectionInLine(lineId);

        sectionDao.deleteLastSection(lineId, stationId);
    }

    private void validateLineAndLastStation(final long lineId, final long stationId) {
        final Section lastSection = sectionDao.findLastSection(lineId)
                .orElseThrow(() -> new IllegalSectionException("해당 노선은 생성되지 않았습니다."));

        if (!lastSection.getDownStationId().equals(stationId)) {
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
