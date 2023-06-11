package subway.domain.service.validator;

import org.springframework.stereotype.Service;
import subway.domain.entity.Section;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;

import java.util.List;

@Service
public class SubwayValidator {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SubwayValidator(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void validateLine(Long lineId) {
        lineRepository.findById(lineId);
    }

    public void validateStations(List<Long> stationIds) {
        stationIds.forEach(stationRepository::findById);
    }

    public void validateNewSection(Long lineId, Section section) {
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
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

    public void validateSectionForDelete(Long lineId, Long stationId) {
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        if (sections.isEmpty()) {
            throw new IllegalStateException("등록된 구간이 없습니다.");
        }

        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.getDownStationId().equals(stationId)) {
            throw new IllegalArgumentException("노선의 하행 종점역만 제거할 수 있습니다.");
        }
    }
}
