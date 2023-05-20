package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line saveLine(Line line) {
        return lineDao.insert(new Line(line.getName(), line.getColor()));
    }

    public List<Line> findLines() {
        return lineDao.findAll();
    }

    public Line findLineById(Long id) {
        try {
            return lineDao.findById(id);
        } catch (Exception e) {
            throw new NoSuchElementException("존재하지 않는 노선입니다.");
        }
    }

    public void updateLine(Long id, Line newLine) {
        lineDao.update(new Line(id, newLine.getName(), newLine.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }
}
