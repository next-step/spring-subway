package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse saveSection(final Long lineId, final SectionRequest sectionRequest) {
        validateLine(lineId, sectionRequest.getUpStationId());
        validateDownLastStation(lineId, sectionRequest.getUpStationId());

        final Section section = sectionDao.insert(sectionRequest.toEntity(lineId));
        return SectionResponse.of(section);
    }

    private void validateDownLastStation(final Long lineId, final Long upStationId) {
        sectionDao.findByUpStationIdAndLineId(lineId, upStationId)
                .ifPresent(section -> {
                    throw new IllegalArgumentException("새로운 구간의 상행 역은 해당 노선에 등록되어있는 하행 종점역이어야 힙니다.");
                });
    }

    private void validateLine(final Long lineId, final Long downStationId) {
        sectionDao.findByDownStationIdAndLineId(lineId, downStationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선에 추가할 수 없습니다."));
    }
}
