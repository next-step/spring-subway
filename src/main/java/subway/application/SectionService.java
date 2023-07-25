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

        if (sections.isFirstStation(sectionRequest.getDownStationId()) ||
                sections.isLastStation(sectionRequest.getUpStationId())) {
            final Section section = sectionDao.insert(sectionRequest.toSection(lineId));
            return SectionResponse.of(section);
        }

        return SectionResponse.of(insertBetween(sections, sectionRequest));
    }

    private Section insertBetween(final Sections sections, final SectionRequest sectionRequest) {
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();
        final Section targetSection = sections.getContainStationSection(upStationId, downStationId);

        validateDistance(targetSection.subtractDistance(sectionRequest.getDistance()));
        final Section requestSection = sectionRequest.toSection(targetSection.getLineId());
        final Section remainSection = targetSection.subtract(requestSection);

        final Section result = sectionDao.insert(requestSection);
        sectionDao.insert(remainSection);
        sectionDao.delete(targetSection.getId());

        return result;
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateSectionsSizeIsNotOne(sections);
        validateSectionsHasNotStation(sections, stationId);

        if (sections.isFirstStation(stationId)) {
            final Section firstSection = sections.getFirstSection();
            sectionDao.delete(firstSection.getId());
            return;
        } else if (sections.isLastStation(stationId)) {
            final Section lastSection = sections.getLastSection();
            sectionDao.delete(lastSection.getId());
            return;
        }

        final Section nextBetweenSection = sections.getBetweenSectionToNext(stationId);
        final Section prevBetweenSection = sections.getBetweenSectionToPrev(stationId);

        sectionDao.delete(nextBetweenSection.getId());
        sectionDao.delete(prevBetweenSection.getId());
        sectionDao.insert(nextBetweenSection.merge(prevBetweenSection));
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

    private void validateNotContainsBothStation(final Sections sections, final SectionRequest sectionRequest) {
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();
        if (sections.containsBoth(upStationId, downStationId)) {
            throw new SubwayException(
                    "상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다. 상행 역 ID : " + upStationId + " 하행 역 ID : " + downStationId);
        }
    }

    private void validateSectionsHasNotStation(final Sections sections, final Long stationId) {
        if (!sections.containsStation(stationId)) {
            throw new SubwayException("해당 구간에는 해당 역이 존재하지 않아서 제거할 수 없습니다. 역 ID : " + stationId);
        }
    }
}
