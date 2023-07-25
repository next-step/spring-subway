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
        validateLineInStation(lineId, stationId);
        validateSectionInLine(lineId);

        // 노선의 모든 구간 조회 -> 종점역도 포함해서 가져와야하므로 역과 연결된 노선만 가져오기 X
        final Sections sections = new Sections(sectionDao.findAll(lineId));

        // 종점역일 경우
        if (sections.isLastStation(stationId)) {
            // 종점역이 연결된 구간 삭제
            final Section connectedSection = sections.getLastSection(stationId);
            sectionDao.delete(connectedSection.getId());
        }
        // 종점역이 아닐 경우
        else {
            Section leftSection = sections.findLeftSection(stationId);
            Section rightSection = sections.findRightStation(stationId);

            // 구간 한 개 삭제
            sectionDao.delete(leftSection.getId());

            // 나머지 구간은 갱신
            Section newSection = new Section(rightSection.getId(), rightSection.getLineId(),
                // 상행역을 leftSection 의 상행역과 연결
                leftSection.getUpStationId(), rightSection.getDownStationId(),
                leftSection.getDistance()+ rightSection.getDistance());
            sectionDao.update(newSection);
        }
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
