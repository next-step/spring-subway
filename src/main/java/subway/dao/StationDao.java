package subway.dao;

import java.util.List;
import java.util.Optional;
import subway.domain.Station;

public interface StationDao {

    Optional<Station> insert(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    void update(Station newStation);

    void deleteById(Long id);

    boolean existByName(String name);
}
