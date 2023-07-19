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

    public SectionResponse saveSection(final long lineId, final SectionRequest sectionRequest) {
        validate(lineId, sectionRequest);

        final Section section = sectionDao.insert(sectionRequest.to(lineId));
        return SectionResponse.of(section);
    }

    private void validate(final long lineId, final SectionRequest sectionRequest) {
        validateLineAndLastStation(lineId, sectionRequest.getUpStationId());
        validateDuplicateStationInLine(lineId, sectionRequest.getDownStationId());
    }

    private void validateLineAndLastStation(final long lineId, final long upStationId) {
        final Section lastSection = sectionDao.findLastSection(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 생성되지 않았습니다."));

        if(!lastSection.getDownStationId().equals(upStationId)) {
            throw new IllegalArgumentException("새로운 구간의 상행 역은 해당 노선에 등록되어있는 하행 종점역이어야 힙니다.");
        }
    }

    private void validateDuplicateStationInLine(final long lineId, final long stationId) {
        sectionDao.findByLineIdAndStationId(lineId, stationId)
                .ifPresent(section -> {
                    throw new IllegalArgumentException("새로운 구간의 하행 역은 해당 노선에 등록되어있는 역일 수 없습니다.");
                });
    }
}
