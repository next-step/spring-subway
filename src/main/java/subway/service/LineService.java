package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.entity.Line;
import subway.domain.repository.LineRepository;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line saveLine(Line line) {
        return lineRepository.insert(new Line(line.getName(), line.getColor()));
    }

    public List<Line> findLines() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id);
    }

    public void updateLine(Long id, Line newLine) {
        lineRepository.update(new Line(id, newLine.getName(), newLine.getColor()));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
