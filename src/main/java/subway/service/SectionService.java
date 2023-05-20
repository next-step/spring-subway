package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Section;

import java.util.List;

@Service
public class SectionService {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(LineService lineService, StationService stationService, SectionDao sectionDao) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public Section saveSection(Long lineId, Section section) {
        validateLine(lineId);
        validateStations(List.of(section.getUpStationId(), section.getDownStationId()));
        validateNewSection(lineId, section);
        return sectionDao.insert(section);
    }

    public List<Section> findAllSectionsByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }

    private void validateLine(Long lineId) {
        lineService.findLineById(lineId);
    }

    private void validateStations(List<Long> stationIds) {
        stationIds.forEach(stationService::findStationById);
    }

    private void validateNewSection(Long lineId, Section section) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        if (!sections.isEmpty()) {
            Section lastSection = sections.get(sections.size() - 1);
            if (!lastSection.getDownStationId().equals(section.getUpStationId())) {
                throw new IllegalArgumentException("노선의 하행 종점역이 새 구간의 상행역과 같지 않습니다.");
            }
            if (sections.stream().anyMatch(s -> s.getUpStationId().equals(section.getDownStationId()))) {
                throw new IllegalArgumentException("이미 등록된 역입니다.");
            }
        }
    }
}
