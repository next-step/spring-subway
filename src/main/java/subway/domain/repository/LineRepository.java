package subway.domain.repository;

import subway.domain.Line;

import java.util.List;

public interface LineRepository {
    Line insert(Line line);
    void update(Line line);
    List<Line> findAll();
    Line findById(Long id);
    void deleteById(Long id);
}
