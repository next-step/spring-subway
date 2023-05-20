package subway.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.SubwayGraph;
import subway.domain.dto.AddSectionDto;
import subway.domain.dto.LineDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.domain.vo.Distance;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubwayGraphService {
    SubwayGraph subwayGraph = new SubwayGraph();
    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    /**
     * DB의 구간 데이터를 조회하여 그래프 상태를 초기화한다.
     */
    public void initGraph() {
        List<Section> allSection = sectionRepository.findAll();
        for (Section section : allSection) {
            subwayGraph.add(section);
        }
    }

    public Section addSection(AddSectionDto addSectionDto) {
        Line line = lineRepository.findById(addSectionDto.getLineId());
        Station upStation = stationRepository.findById(addSectionDto.getUpStationId());
        Station downStation = stationRepository.findById(addSectionDto.getDownStationId());
        Integer distance = addSectionDto.getDistance();
        Section section = Section.of(line, upStation, downStation, distance);

        subwayGraph.add(section);
        return sectionRepository.insert(section);
    }

    public void removeStation(Long stationId) {
        subwayGraph.remove(stationId);
        sectionRepository.deleteById(stationId);
    }

    public LineDto getLineWithStations(Long lineId) {
        Line line = lineRepository.findById(lineId);
        return LineDto.from(subwayGraph.getLineWithStations(line));
    }
}
