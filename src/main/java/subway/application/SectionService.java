package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SubwayException;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateRequest(sections, sectionRequest);

        final Section lastPrevSection = sections.findLastPrevSection()
                .orElseThrow(() -> new SubwayException("노선에 구간이 존재하지 않습니다."));
        if (!lastPrevSection.isSameDownStationId(sectionRequest.getUpStationId())) {
            throw new SubwayException("새로운 구간의 상행역이 해당 노선에 등록되어 있는 하행 종점역이 아닙니다.");
        }

        final Section section = sectionDao.insert(sectionRequest.toSection(lineId, lastPrevSection.getId()));
        sectionDao.updatePrevSectionId(lastPrevSection.getId(), section.getId());

        return SectionResponse.of(section);
    }

    public void deleteSection(final Long lineId, final Long downStationId) {
        sectionDao.delete(lineId, downStationId);
    }

    private void validateRequest(final Sections sections, final SectionRequest sectionRequest) {
        if (!sections.containsStation(sectionRequest.getUpStationId())) {
            throw new SubwayException("새로운 구간의 상행역이 해당 노선에 등록되어 있지 않습니다.");
        }
        if (sections.containsStation(sectionRequest.getDownStationId())) {
            throw new SubwayException("새로운 구간의 하행역이 해당 노선에 등록되어 있습니다.");
        }
    }
}
