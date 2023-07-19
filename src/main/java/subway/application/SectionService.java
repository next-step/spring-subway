package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Section;
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
        validateLineAndLastStation(lineId, sectionRequest.getUpStationId());
        validateDuplicateStationInLine(lineId, sectionRequest.getDownStationId());

        final Section section = sectionDao.insert(sectionRequest.to(lineId));
        return SectionResponse.of(section);
    }

    public void deleteSection(final long lineId, final long stationId) {
        validateLineAndLastStation(lineId, stationId);

        sectionDao.deleteLastSection(lineId, stationId);
    }

    private void validateLineAndLastStation(final long lineId, final long stationId) {
        final Section lastSection = sectionDao.findLastSection(lineId)
                .orElseThrow(() -> new IllegalSectionException("해당 노선은 생성되지 않았습니다."));

        if(!lastSection.getDownStationId().equals(stationId)) {
            throw new IllegalSectionException("해당 역은 노선의 하행 종점역이 아닙니다.");
        }
    }

    private void validateDuplicateStationInLine(final long lineId, final long stationId) {
        sectionDao.findByLineIdAndStationId(lineId, stationId)
                .ifPresent(section -> {
                    throw new IllegalSectionException("새로운 구간의 하행 역은 해당 노선에 등록되어있는 역일 수 없습니다.");
                });
    }
}
