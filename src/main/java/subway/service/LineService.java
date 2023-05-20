package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.repository.LineRepository;
import subway.jdbcdao.LineDao;
import subway.domain.entity.Line;

import java.util.List;
import java.util.NoSuchElementException;

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
        try {
            return lineRepository.findById(id);
        } catch (Exception e) {
            throw new NoSuchElementException("존재하지 않는 노선입니다.");
        }
    }

    public void updateLine(Long id, Line newLine) {
        lineRepository.update(new Line(id, newLine.getName(), newLine.getColor()));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
