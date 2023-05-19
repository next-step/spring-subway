package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;

import java.util.List;

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
        return lineDao.findById(id);
    }

    public void updateLine(Long id, Line newLine) {
        lineDao.update(new Line(id, newLine.getName(), newLine.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }
}
