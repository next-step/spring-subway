package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.entity.Section;
import subway.domain.entity.Station;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.service.validator.SubwayValidator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final SubwayValidator subwayValidator;

    public SectionService(StationRepository stationRepository, SectionRepository sectionRepository, SubwayValidator subwayValidator) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
        this.subwayValidator = subwayValidator;
    }

    public Section saveSection(Long lineId, Section section) {
        subwayValidator.validateLine(lineId);
        subwayValidator.validateStations(List.of(section.getUpStationId(), section.getDownStationId()));
        subwayValidator.validateNewSection(lineId, section);
        return sectionRepository.insert(section);
    }

    public void deleteSectionByStationId(Long lineId, Long stationId) {
        subwayValidator.validateSectionForDelete(lineId, stationId);
        sectionRepository.deleteByStationId(lineId, stationId);
    }

    public List<Station> findAllStationsByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(section -> stationRepository.findById(section.getUpStationId()))
                .sorted(Comparator.comparing(Station::getId))
                .collect(Collectors.toList());
        stations.add(stationRepository.findById(sections.get(sections.size() - 1).getDownStationId()));
        return stations;
    }
}
