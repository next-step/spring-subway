package subway.dao;

import java.util.List;
import java.util.Optional;
import subway.domain.Line;

public interface LineDao {

    Line insert(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Line newLine);

    void deleteById(Long id);

    boolean existByName(String name);
}
