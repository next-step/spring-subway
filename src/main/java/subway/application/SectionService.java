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
        validateNotContainsBothStation(sections, sectionRequest);

        if (sections.isEndStation(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            final Section section = sectionDao.insert(sectionRequest.toSection(lineId));
            return SectionResponse.of(section);
        }

        return SectionResponse.of(insertBetween(sections, sectionRequest));
    }

    private Section insertBetween(final Sections sections, final SectionRequest sectionRequest) {
        final Section targetSection =
                sections.findContainStationSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())
                        .orElseThrow(() -> new SubwayException("상행 역과 하행 역이 모두 노선에 없습니다."));

        validateDistance(targetSection.subtractDistance(sectionRequest.getDistance()));
        final Section requestSection = sectionRequest.toSection(targetSection.getLineId());
        final Section remainSection = targetSection.subtract(requestSection);

        final Section result = sectionDao.insert(requestSection);
        sectionDao.insert(remainSection);
        sectionDao.delete(targetSection.getId());

        return result;
    }

    public void deleteSection(final Long lineId, final Long downStationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateSectionsSizeIsNotOne(sections);

        final Section lastSection = getLastSection(sections);
        if (!lastSection.isSameDownStationId(downStationId)) {
            throw new SubwayException("해당 노선에 일치하는 하행 종점역이 존재하지 않습니다.");
        }

        sectionDao.delete(lastSection.getId());
    }

    private Section getLastSection(final Sections sections) {
        return sections.findLastSection()
                .orElseThrow(() -> new SubwayException("노선에 구간이 존재하지 않습니다."));
    }

    private void validateNotContainsBothStation(final Sections sections, final SectionRequest sectionRequest) {
        if (sections.containsBoth(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new SubwayException("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private void validateDistance(final Long distance) {
        if (distance <= 0) {
            throw new SubwayException("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        }
    }

    private void validateSectionsSizeIsNotOne(final Sections sections) {
        if (sections.isEqualSizeToOne()) {
            throw new SubwayException("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
        }
    }
}
