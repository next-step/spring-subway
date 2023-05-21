package subway.testdouble;

import subway.domain.entity.Line;
import subway.domain.repository.LineRepository;

import java.util.*;

public class InMemoryLineRepository implements LineRepository {
    private final Map<Long, Line> lineMap = new HashMap<>();

    @Override
    public Line insert(Line line) {
        lineMap.put(line.getId(), line);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lineMap.values());
    }

    @Override
    public Line findById(Long id) {
        Line line = lineMap.get(id);
        if (line == null) {
            throw new NoSuchElementException("존재하지 않는 노선입니다.");
        }
        return line;
    }

    @Override
    public void update(Line newLine) {
        lineMap.put(newLine.getId(), newLine);
    }

    @Override
    public void deleteById(Long id) {
        lineMap.remove(id);
    }
}
