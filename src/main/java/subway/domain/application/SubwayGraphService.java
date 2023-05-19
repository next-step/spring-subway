package subway.domain.application;

import org.springframework.stereotype.Service;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SubwayGraph;
import subway.domain.dto.LineDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;

import java.util.List;

@Service
public class SubwayGraphService {
    SubwayGraph subwayGraph;
    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    public SubwayGraphService(SectionRepository sectionRepository, LineRepository lineRepository) {
        subwayGraph = new SubwayGraph();
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        initGraph();
    }

    private void initGraph() {
        List<Section> allSection = sectionRepository.findAll();
        for (Section section : allSection) {
            subwayGraph.add(section);
        }
    }

    public Section addSection(Section section) {
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
