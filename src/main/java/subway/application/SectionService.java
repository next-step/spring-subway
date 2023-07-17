package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        if (sectionDao.existByLineId(lineId)) {
            Section lastSection = sectionDao.findLastSection(lineId);
            if (!lastSection.getDownStationId().equals(request.getUpStationId())) {
                throw new IllegalArgumentException("하행 종점역과 새로운 구간의 상행역은 같아야합니다.");
            }
        }

        if (sectionDao.existByLineIdAndStationId(lineId, request.getDownStationId())) {
            throw new IllegalArgumentException("새로운 구간 하행역이 기존 노선에 존재하면 안됩니다.");
        }

        Section section = sectionDao.insert(request.toEntity(lineId));
        return SectionResponse.from(section);
    }
}
