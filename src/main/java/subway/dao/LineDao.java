package subway.dao;

import subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Optional<Line> insert(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Line newLine);

    void deleteById(Long id);
}
