package subway.domain.service;

import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            lineRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("구간이 등록된 노선은 삭제할 수 없습니다. 구간 삭제 후 다시 시도해주세요.");
        }
    }
}
