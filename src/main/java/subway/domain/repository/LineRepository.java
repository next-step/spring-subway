package subway.domain.repository;

import subway.domain.entity.Line;

import java.util.List;

public interface LineRepository {

    Line insert(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line newLine);

    void deleteById(Long id);
}
